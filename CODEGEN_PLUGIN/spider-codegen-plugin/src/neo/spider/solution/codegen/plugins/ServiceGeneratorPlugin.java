package neo.spider.solution.codegen.plugins;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import neo.spider.solution.codegen.util.StringUtil;

public class ServiceGeneratorPlugin extends PluginAdapter {

	@Override
	public boolean validate(List<String> warnings) {
		// 플러그인 유효성 검증
		if(properties.getProperty("targetProject") == null) {
			return false;
		}
		return true;
	}
	
	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
		List<GeneratedJavaFile> fileList = new ArrayList<GeneratedJavaFile>();
		
		// 원래 테이블 이름 가져오기
        String tableName = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
        // DTO 타입 가져오기
        String dtoType = introspectedTable.getBaseRecordType();
        // Mapper 타입 가져오기
        String mapperType = introspectedTable.getMyBatis3JavaMapperType();

    	// primary Key column
    	IntrospectedColumn primaryKeyColumn = null;
    	if(introspectedTable.hasPrimaryKeyColumns()) {
    		primaryKeyColumn = introspectedTable.getPrimaryKeyColumns().get(0);
    	}
    	
    	// Service 클래스 생성
    	TopLevelClass serviceClass = generateServiceClass(tableName, dtoType, mapperType, primaryKeyColumn);
    	
    	// Java 파일 생성
    	GeneratedJavaFile serviceFile = new GeneratedJavaFile(serviceClass, properties.getProperty("targetProject"), context.getJavaFormatter());
    	
		fileList.add(serviceFile);
		
		return fileList;
	}
	
	private TopLevelClass generateServiceClass(String tableName, String dtoType, String mapperType, IntrospectedColumn primaryKeyColumn) {
		String className = StringUtil.toPascalCase(tableName);
        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType("com.example.service." + className + "Service");

        // 클래스 생성
        TopLevelClass serviceClass = new TopLevelClass(serviceType);
        serviceClass.setVisibility(JavaVisibility.PUBLIC);
        serviceClass.addImportedType(dtoType);
        serviceClass.addImportedType(mapperType);
        serviceClass.addImportedType(FullyQualifiedJavaType.getNewListInstance());
        serviceClass.addImportedType("org.springframework.stereotype.Service");
        serviceClass.addImportedType("org.springframework.beans.factory.annotation.Autowired");
        
        // @Service 어노테이션 추가
        serviceClass.addAnnotation("@Service");
        
        // Mapper 필드 추가
        Field mapperField = new Field("mapper", new FullyQualifiedJavaType(mapperType));
        mapperField.setVisibility(JavaVisibility.PRIVATE);
        mapperField.addAnnotation("@Autowired");
        serviceClass.addField(mapperField);
        
        // 메서드 추가: insert
        Method insertMethod = new Method("create");
        insertMethod.setVisibility(JavaVisibility.PUBLIC);
        insertMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        insertMethod.addParameter(new Parameter(new FullyQualifiedJavaType(dtoType), "dto"));
        insertMethod.addBodyLine("return mapper.insert(dto);");
        serviceClass.addMethod(insertMethod);
        
        // 메서드 추가: insertSelective
        Method insertSelectiveMethod = new Method("createSelective");
        insertSelectiveMethod.setVisibility(JavaVisibility.PUBLIC);
        insertSelectiveMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        insertSelectiveMethod.addParameter(new Parameter(new FullyQualifiedJavaType(dtoType), "dto"));
        insertSelectiveMethod.addBodyLine("return mapper.insertSelective(dto);");
        serviceClass.addMethod(insertSelectiveMethod);

        // 메서드 추가: find
        Method findMethod = new Method("find" + className);
        findMethod.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("List");
        returnType.addTypeArgument(new FullyQualifiedJavaType(dtoType));
        findMethod.setReturnType(returnType);
        findMethod.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "page"));
        findMethod.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "size"));
        findMethod.addParameter(new Parameter(new FullyQualifiedJavaType(dtoType), "dto"));
        findMethod.addBodyLine("int offset = (page - 1) * size;");
        findMethod.addBodyLine("return mapper.find" + className + "(offset, size, dto);");
        serviceClass.addMethod(findMethod);
        
        // 메서드 추가: count
        Method countMethod = new Method("count" + className);
        countMethod.setVisibility(JavaVisibility.PUBLIC);
        countMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        countMethod.addParameter(new Parameter(new FullyQualifiedJavaType(dtoType), "dto"));
        countMethod.addBodyLine("return mapper.count" + className + "(dto);");
        serviceClass.addMethod(countMethod);
        
        // 기본키가 존재하면 primaryKey 관련 메서드 생성
        if(primaryKeyColumn != null) {
        	FullyQualifiedJavaType pkJavaType = primaryKeyColumn.getFullyQualifiedJavaType();
        	String pkName = primaryKeyColumn.getJavaProperty();
        	
        	// 메서드 추가: deleteByPrimaryKey
        	Method deleteMethod = new Method("deleteBy" + StringUtil.toPascalCase(pkName));
        	deleteMethod.setVisibility(JavaVisibility.PUBLIC);
        	deleteMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        	deleteMethod.addParameter(new Parameter(pkJavaType, pkName));
        	deleteMethod.addBodyLine("return mapper.deleteByPrimaryKey(" + pkName + ");");
        	serviceClass.addMethod(deleteMethod);
        	
        	// 메서드 추가: selectByPrimaryKey
        	Method selectMethod = new Method("findBy" + StringUtil.toPascalCase(pkName));
        	selectMethod.setVisibility(JavaVisibility.PUBLIC);
        	selectMethod.setReturnType(new FullyQualifiedJavaType(dtoType));
        	selectMethod.addParameter(new Parameter(pkJavaType, pkName));
        	selectMethod.addBodyLine("return mapper.selectByPrimaryKey(" + pkName + ");");
        	serviceClass.addMethod(selectMethod);

        	// 메서드 추가: updateByPrimaryKeySelective
        	Method updateSelectiveMethod = new Method("updateBy" + StringUtil.toPascalCase(pkName) + "Selective");
        	updateSelectiveMethod.setVisibility(JavaVisibility.PUBLIC);
        	updateSelectiveMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        	updateSelectiveMethod.addParameter(new Parameter(new FullyQualifiedJavaType(dtoType), "dto"));
        	updateSelectiveMethod.addBodyLine("return mapper.updateByPrimaryKeySelective(dto);");
        	serviceClass.addMethod(updateSelectiveMethod);

        	// 메서드 추가: updateByPrimaryKey
        	Method updateMethod = new Method("updateBy" + StringUtil.toPascalCase(pkName));
        	updateMethod.setVisibility(JavaVisibility.PUBLIC);
        	updateMethod.setReturnType(FullyQualifiedJavaType.getIntInstance());
        	updateMethod.addParameter(new Parameter(new FullyQualifiedJavaType(dtoType), "dto"));
        	updateMethod.addBodyLine("return mapper.updateByPrimaryKey(dto);");
        	serviceClass.addMethod(updateMethod);
        }

		return serviceClass;
	}
}
