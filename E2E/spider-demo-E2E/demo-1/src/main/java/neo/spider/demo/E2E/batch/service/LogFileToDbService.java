package neo.spider.demo.E2E.batch.service;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import org.springframework.stereotype.Service;

@Service
public class LogFileToDbService {

   // 배치 사이즈
   private static final int BatchSize = 100;

   // 로깅 sql
   private final String eventSql = "INSERT INTO FWK_E2E_LOGGING_EVENT (TIMESTAMP, TRACE_ID, REQUEST_URL, USER_ID, USER_IP, USER_DEVICE_CD, CALLER_COMPONENT_NAME, TARGET_COMPONENT_NAME, EXECUTION_TIME, RESPONSE_STATUS_CD, ERROR_MESSAGE_TEXT, DELAY_MESSAGE_TEXT, QUERY, FILE_SEQUENCE) "
         + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

   private final String slowSql = "INSERT INTO FWK_E2E_LOGGING_DELAY (TIMESTAMP, TRACE_ID, REQUEST_URL, USER_ID, USER_IP, USER_DEVICE_CD, CALLER_COMPONENT_NAME, TARGET_COMPONENT_NAME, EXECUTION_TIME, RESPONSE_STATUS_CD, ERROR_MESSAGE_TEXT, DELAY_MESSAGE_TEXT, QUERY) "
         + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

   private final String errorSql = "INSERT INTO FWK_E2E_LOGGING_ERROR (TIMESTAMP, TRACE_ID, REQUEST_URL, USER_ID, USER_IP, USER_DEVICE_CD, CALLER_COMPONENT_NAME, TARGET_COMPONENT_NAME, EXECUTION_TIME, RESPONSE_STATUS_CD, ERROR_MESSAGE_TEXT, DELAY_MESSAGE_TEXT, QUERY) "
         + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

   // prepareStatement 생성
   private PreparedStatement preparedStatement(Connection connection, String sql) throws SQLException {

      PreparedStatement ps = null;

      try {
         ps = connection.prepareStatement(sql);
      } catch (SQLException e) {
         throw e;
      }
      return ps;
   }

   // sql문 순서에 맞춘 ps 세팅
   private void setPreparedStatement(PreparedStatement ps, String[] tokens, int[] indices) throws SQLException {

      // int[] indices = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };
      for (int i = 0; i < indices.length; i++) {

         // 값이 없을 경우 null로 인서트
         if (tokens[indices[i]].equals("")) {
            ps.setObject(i + 1, null);
         } else if (i != 0 && i != 8) {
            ps.setString(i + 1, tokens[indices[i]]);
         } else { // TIMESTAMP, EXECUTIONTIME : long
            ps.setLong(i + 1, Long.parseLong(tokens[indices[i]]));
         }
      }
   }

   // ps 세팅
   private PreparedStatement setPs(PreparedStatement eventPs, String[] tokens) throws SQLException {
      // 3번째 토큰부터 시작
      int[] indices = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };
      setPreparedStatement(eventPs, tokens, indices);

      return eventPs;
   }

   // ps 커넥션 닫기
   private void closeConnection(PreparedStatement... statements) throws SQLException {

      for (PreparedStatement ps : statements) {
         if (ps != null) {
            ps.close();
         }
      }
   }

   // 스트링 토큰 정리
   private String[] getTokens(String line) {
      // 한 row를 ;로 나눈 다음 null인 경우 빈 스트링으로 변환한 후 공백 제거
      return Arrays.stream(line.split(";")).map(token -> token == null ? "" : token.trim()).toArray(String[]::new);
   }

   // 배치에서 호출할 메서드
   public void executeLogFileToDb(Connection connection, String filePath) throws Exception {

      long start = System.currentTimeMillis();
      System.out.println("시작 : " + start);
      connection.setAutoCommit(false);

      try (LineNumberReader lineReader = new LineNumberReader(new FileReader(filePath));
            PreparedStatement eventPs = preparedStatement(connection, eventSql);
            PreparedStatement slowPs = preparedStatement(connection, slowSql);
            PreparedStatement errorPs = preparedStatement(connection, errorSql);) {
         
         String line;
         int eventCount = 0, slowCount = 0, errorCount = 0;

         while ((line = lineReader.readLine()) != null) {

            String[] tokens = getTokens(line);

            // 조건 분류
            String condition = "";
            if (!tokens[13].equals("")) {
               condition += "SLOW";
            }
            if (!tokens[12].equals("")) {
               condition += "ERROR";
            }

            // 조건으로 분류
            switch (condition) {

            case "SLOWERROR":

               // logging slow
               setPs(slowPs, tokens);
               slowCount++;
               slowPs.addBatch();

               // logging error
               setPs(errorPs, tokens);
               errorCount++;
               errorPs.addBatch();

               break;

            case "SLOW":

               // logging slow
               setPs(slowPs, tokens);
               slowCount++;
               slowPs.addBatch();

               break;

            case "ERROR":

               // logging error
               setPs(errorPs, tokens);
               errorCount++;
               errorPs.addBatch();

               break;

            }

            // logging event
            setPs(eventPs, tokens);

            // seq
            eventPs.setLong(14, lineReader.getLineNumber());
            eventCount++;
            eventPs.addBatch();

            // 100개 도달 시 배치 실행
            executeBatches(eventCount, slowCount, errorCount, eventPs, slowPs, errorPs, connection);

         }

         finalizeBatches(eventCount, slowCount, errorCount, eventPs, slowPs, errorPs, connection);

      } catch (Exception e) {
         // 예외 발생 시 롤백
         connection.rollback();
         throw e;
      } finally {
         // 연결 닫기
//         closeConnection(eventPs, slowPs, errorPs);
      }

      long end = System.currentTimeMillis();
      System.out.println("끝 : " + end);
      System.out.println("경과시간 : " + ((end - start) / 1000) + "s");
   }

   // 100개 도달 시 배치 실행
   private void executeBatch(int count, PreparedStatement preparedStatement, Connection connection)
         throws SQLException {

      if (count > 0 && count % BatchSize == 0) {
         preparedStatement.executeBatch();
         connection.commit();
      }
   }

   // 100개 도달하는 배치들 실행
   private void executeBatches(int eventCount, int slowCount, int errorCount, PreparedStatement eventPs,
         PreparedStatement slowPs, PreparedStatement errorPs, Connection connection) throws SQLException {

      // 이벤트 테이블에 적재
      executeBatch(eventCount, eventPs, connection);
      // 슬로우 테이블에 적재
      executeBatch(slowCount, slowPs, connection);
      // 에러 테이블에 적재
      executeBatch(errorCount, errorPs, connection);

   }

   private void finalizeBatch(int count, PreparedStatement ps, Connection connection) throws SQLException {
      if (count % BatchSize != 0) {
         ps.executeBatch();
         connection.commit();
      }
   }

   // 100개를 채우지 못한 나머지 데이터
   private void finalizeBatches(int eventCount, int slowCount, int errorCount, PreparedStatement eventPs,
         PreparedStatement slowPs, PreparedStatement errorPs, Connection connection) throws SQLException {

      // 마지막 event 배치 커밋
      finalizeBatch(eventCount, eventPs, connection);
      // 마지막 slow 배치 커밋
      finalizeBatch(slowCount, slowPs, connection);
      // 마지막 error 배치 커밋
      finalizeBatch(errorCount, errorPs, connection);

   }

}
