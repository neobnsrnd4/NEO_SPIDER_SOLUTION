package neo.spider.solution.codegen.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import neo.spider.solution.codegen.dto.ColumnDTO;
import neo.spider.solution.codegen.dto.TableDTO;

public class DatabaseConnector {

	public static List<TableDTO> getTablesInfo(String dbUrl, String userId, String password) throws SQLException {

		// 데이터베이스 드라이버 등록, 연결할 데이터베이스에 따라 수정 필요
		DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());

		List<String> tableNames = new ArrayList<String>();
		List<TableDTO> tableList = new ArrayList<TableDTO>();

		try (Connection conn = DriverManager.getConnection(dbUrl, userId, password)) {
			String showTablesSql = "SHOW TABLES";

			// 테이블 이름 조회
			try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(showTablesSql)) {
				while (rs.next()) {
					tableNames.add(rs.getString(1));
				}
			}
			
			// table을 조회할 schema 이름 추출, DB URL 입력 시 스키마까지 입력 필수
			int lastSlashIndex = dbUrl.lastIndexOf('/'); // 마지막 '/' 위치를 찾음
			int firstQuestionIndex = dbUrl.indexOf('?') > 0 ? dbUrl.indexOf('?') : dbUrl.length(); // 첫번째 '?' 위치를 찾음
			String schemaName = dbUrl.substring(lastSlashIndex + 1, firstQuestionIndex); // '/' 이후 문자열 추출
			String columnInfoSql = """
				    SELECT COLUMN_NAME, DATA_TYPE,
				           IF(COLUMN_KEY = 'PRI', TRUE, FALSE) AS IS_PRIMARY_KEY
				    FROM INFORMATION_SCHEMA.COLUMNS
				    WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?
				    ORDER BY ORDINAL_POSITION
				""";
			
			// 테이블 컬럼 정보 조회
			try (PreparedStatement stmt = conn.prepareStatement(columnInfoSql)) {
                for (String tableName : tableNames) {
                    stmt.setString(1, schemaName);
                    stmt.setString(2, tableName);

                    try (ResultSet rs = stmt.executeQuery()) {
                    	TableDTO table = new TableDTO();
                    	List<ColumnDTO> columnList = new ArrayList<ColumnDTO>();
                        
                        while (rs.next()) {
                        	ColumnDTO column = new ColumnDTO(rs.getString("COLUMN_NAME"), rs.getString("DATA_TYPE"), rs.getBoolean("IS_PRIMARY_KEY"));
                        	columnList.add(column);
                        }
                        
                        table.setTableName(tableName);
                        table.setColumns(columnList);
                        tableList.add(table);
                    }
                }
            }
		}

		return tableList;
	}
}
