package neo.spider.demo.E2E.jpa;

import org.hibernate.resource.jdbc.spi.StatementInspector;

public class ProjectStatementInspector implements StatementInspector{

	@Override
	public String inspect(String sql) {
		// TODO Auto-generated method stub
		
		if(sql != null) {
			String trimmedSql = sql.trim();
			if(trimmedSql.toLowerCase().startsWith("select")) {
				return " /* modified by QueryModifierInspector */" + trimmedSql ;
			}
		}
		return sql;
	}

}
