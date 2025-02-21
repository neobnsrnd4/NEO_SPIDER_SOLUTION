package neo.spider.solution.codegen.dto;

public class ColumnDTO {
	private String columnName;
	private String dataType;
	private Boolean isPrimaryKey;
	
	public ColumnDTO(String columnName, String dataType, Boolean isPrimaryKey) {
		super();
		this.columnName = columnName;
		this.dataType = dataType;
		this.isPrimaryKey = isPrimaryKey;
	}
	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public Boolean getIsPrimaryKey() {
		return isPrimaryKey;
	}
	public void setIsPrimaryKey(Boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}
	
	// javascript 에서 parsing 하기 편하도록 json 형식으로 반환
	@Override
	public String toString() {
		return String.format("{\"columnName\": \"%s\", \"dataType\": \"%s\", \"isPrimaryKey\": %s}",
                columnName,
                dataType,
                isPrimaryKey);
	}
	
}
