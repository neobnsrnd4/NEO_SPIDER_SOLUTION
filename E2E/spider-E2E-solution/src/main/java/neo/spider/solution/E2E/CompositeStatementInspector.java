package neo.spider.solution.E2E;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.resource.jdbc.spi.StatementInspector;

public class CompositeStatementInspector implements StatementInspector{

	private final List<StatementInspector> inspectors = new ArrayList<>();
	
	//Inspector을 체인의 맨 뒤에 추가
	public void addInspector(StatementInspector inspector) {
		inspectors.add(inspector);
	}
	
	 // Inspector를 체인의 맨 앞에 추가
    public void addInspectorAtBeginning(StatementInspector inspector) {
        inspectors.add(0, inspector);
    }
    
    // Inspector를 원하는 위치에 추가
    public void addInspectorAtIndex(int index, StatementInspector inspector) {
        inspectors.add(index, inspector);
    }
	
	@Override
	public String inspect(String sql) {
		// TODO Auto-generated method stub
		String modifiedSql = sql;
		for(StatementInspector inspector : inspectors) {
			modifiedSql = inspector.inspect(modifiedSql);
		}
		return modifiedSql;
	}

}
