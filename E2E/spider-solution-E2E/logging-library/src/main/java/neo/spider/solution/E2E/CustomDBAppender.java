//package neo.spider.solution.E2E.library;
//
//import ch.qos.logback.classic.db.DBAppender;
//import ch.qos.logback.classic.spi.ILoggingEvent;
//
//import org.slf4j.MDC;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//
//public class CustomDBAppender extends DBAppender {
//
//    @Override
//    protected String getInsertSQL() {
//        // Custom SQL for inserting into the 'log' table
//        return "INSERT INTO log (timestmp, call_timestmp, trace_id, request_url, user_id, user_ip, device, called_by, current, execution_time, response_status, error, delay, query) "
//                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//    }
//
//    @Override
//    protected void subAppend(ILoggingEvent event, Connection connection, PreparedStatement stmt) throws SQLException {
//        boolean previousAutoCommitState = connection.getAutoCommit();
//        try {
//            // Auto-commit 활성화 - 트랜잭션 롤백의 영향을 받지 않기 위해
//            connection.setAutoCommit(true);
//
//            // Insert log data into custom log table
//            saveLog(event, stmt);
//
//        } catch (SQLException e) {
//            addError("Failed to process log entry", e);
//            throw e;
//        } finally {
//            // Restore previous Auto-commit state
//            connection.setAutoCommit(previousAutoCommitState);
//        }
//    }
//
//    /**
//     * Custom method to insert logs into the 'log' table
//     */
//    private void saveLog(ILoggingEvent event, PreparedStatement stmt) throws SQLException {
//        
//     // 1. 타임스탬프 추가
//        stmt.setTimestamp(1, new java.sql.Timestamp(event.getTimeStamp()));
//        
//     // 2. 호출 시간 추가
//        String callTimestamp = MDC.get("callTimestamp");
//        
//        stmt.setLong(2, Long.parseLong(callTimestamp));
//        
//        // Getting MDC values for trace_id, user information, etc.
//        String traceId = MDC.get("traceId");
//        String requestUrl = MDC.get("requestURL");
//        String userId = MDC.get("userId");
//        String userIp = MDC.get("userIP");
//        String device = MDC.get("device");
//        String calledBy = MDC.get("calledBy");
//        String current = MDC.get("current");
//        String executionTime = MDC.get("executionTime");
//        String responseStatus = MDC.get("responseStatus");
//        String error = MDC.get("error");
//        String delay = MDC.get("delay");
//        String query = MDC.get("query");
//
//        stmt.setString(3, traceId );
//        stmt.setString(4, requestUrl );
//        stmt.setString(5, userId );
//        stmt.setString(6, userIp );
//        stmt.setString(7, device );
//        stmt.setString(8, calledBy );
//        stmt.setString(9, current );
//        stmt.setString(10, executionTime );
//        stmt.setString(11, responseStatus );
//        stmt.setString(12, error );
//        stmt.setString(13, delay );
//        stmt.setString(14, query );
//
//        // Execute the insert query
//        int result = stmt.executeUpdate();
//        if (result == 0) {
//            addError("No rows inserted into log table.");
//        }
//    }
//}













