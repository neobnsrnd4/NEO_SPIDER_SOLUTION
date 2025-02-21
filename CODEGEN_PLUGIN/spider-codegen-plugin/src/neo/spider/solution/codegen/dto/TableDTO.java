package neo.spider.solution.codegen.dto;

import java.util.List;

public class TableDTO {
	private String tableName;
	private List<ColumnDTO> columns;

	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public List<ColumnDTO> getColumns() {
		return columns;
	}
	public void setColumns(List<ColumnDTO> columns) {
		this.columns = columns;
	}

	// javascript 에서 parsing 하기 편하도록 json 형식으로 반환
	@Override
	public String toString() {
		StringBuilder columnsJson = new StringBuilder("[");
        for (int i = 0; i < columns.size(); i++) {
            columnsJson.append(columns.get(i).toString());
            if (i < columns.size() - 1) {
                columnsJson.append(", ");
            }
        }
        columnsJson.append("]");
        return String.format("{\"tableName\": \"%s\", \"columns\": %s}", tableName, columnsJson);
	}

}
