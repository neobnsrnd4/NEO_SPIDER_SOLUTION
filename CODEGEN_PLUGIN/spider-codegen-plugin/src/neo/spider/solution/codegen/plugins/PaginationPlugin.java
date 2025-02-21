package neo.spider.solution.codegen.plugins;

import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;

import neo.spider.solution.codegen.util.StringUtil;

public class PaginationPlugin extends PluginAdapter {

	@Override
    public boolean validate(List<String> warnings) {
        return true; // 플러그인 유효성 검증
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
    	// 원래 테이블 이름 가져오기
        String tableName = StringUtil.toPascalCase(introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());
    	
    	/*
    	 * find
    	 */
        XmlElement findElement = new XmlElement("select");
        findElement.addAttribute(new Attribute("id", "find" + tableName));
        findElement.addAttribute(new Attribute("resultType", introspectedTable.getBaseRecordType()));
        // SQL 작성
        findElement.addElement(new TextElement("SELECT * FROM " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        findElement.addElement(new TextElement("WHERE 1=1"));
        // 모델 객체의 멤버 변수 조건 추가
        introspectedTable.getAllColumns().forEach(column -> {
            String fieldName = column.getJavaProperty();
            findElement.addElement(new TextElement(
                    "<if test=\"row." + fieldName + " != null\"> AND " + column.getActualColumnName() + " = #{row." + fieldName + "} </if>"
            ));
        });
        // 정렬 처리
        IntrospectedColumn orderByColumn = null;
        if(introspectedTable.hasPrimaryKeyColumns()) {
        	orderByColumn = introspectedTable.getPrimaryKeyColumns().get(0); // 기본키가 있는 경우 첫번째 기본키로 정렬
        }else {
        	orderByColumn = introspectedTable.getAllColumns().get(0); // 기본키가 없는 경우 모든 컬럼 중 첫번째 컬럼으로 정렬
        }
        findElement.addElement(new TextElement("ORDER BY " + orderByColumn.getActualColumnName()));
        // 페이징 처리
        findElement.addElement(new TextElement("LIMIT #{offset}, #{limit}"));
        
        /*
         * count
         */
        XmlElement countElement = new XmlElement("select");
        countElement.addAttribute(new Attribute("id", "count" + tableName));
        countElement.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        // SQL 작성
        countElement.addElement(new TextElement("SELECT count(*) FROM " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        countElement.addElement(new TextElement("WHERE 1=1"));
        // 모델 객체의 멤버 변수 조건 추가
        introspectedTable.getAllColumns().forEach(column -> {
            String fieldName = column.getJavaProperty();
            countElement.addElement(new TextElement(
                    "<if test=\"" + fieldName + " != null\"> AND " + column.getActualColumnName() + " = #{" + fieldName + "} </if>"
            ));
        });
        
        document.getRootElement().addElement(findElement);
        document.getRootElement().addElement(countElement);
        
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
    	// 원래 테이블 이름 가져오기
        String tableName = StringUtil.toPascalCase(introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());
    	// DTO 타입 가져오기
    	FullyQualifiedJavaType dtoType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
    	
		Method findMethod = new Method("find" + tableName);
		findMethod.setVisibility(JavaVisibility.PUBLIC);
		findMethod.setAbstract(true);
		findMethod.setReturnType(new FullyQualifiedJavaType("List<" + introspectedTable.getBaseRecordType() + ">"));
		findMethod.addParameter(createAnnotatedParameter("@Param(\"offset\")", FullyQualifiedJavaType.getIntInstance(), "offset"));
	    findMethod.addParameter(createAnnotatedParameter("@Param(\"limit\")", FullyQualifiedJavaType.getIntInstance(), "limit"));
	    findMethod.addParameter(createAnnotatedParameter("@Param(\"row\")", dtoType, "row"));
	    
	    Method countMethod = new Method("count" + tableName);
	    countMethod.setVisibility(JavaVisibility.PUBLIC);
	    countMethod.setAbstract(true);
	    countMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
	    countMethod.addParameter(new Parameter(dtoType, "row"));

        interfaze.addMethod(findMethod);
        interfaze.addMethod(countMethod);
        
        interfaze.addImportedType(FullyQualifiedJavaType.getNewListInstance());
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));

        return super.clientGenerated(interfaze, introspectedTable);
    }
    
    /**
     * 어노테이션이 포함된 매개변수를 생성
     */
    private Parameter createAnnotatedParameter(String annotation, FullyQualifiedJavaType type, String name) {
        Parameter parameter = new Parameter(type, name);
        parameter.addAnnotation(annotation); // 어노테이션 추가
        return parameter;
    }

}
