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

public class ControllerGeneratorPlugin extends PluginAdapter {

	@Override
	public boolean validate(List<String> warnings) {
		// 플러그인 유효성 검증
		if (properties.getProperty("targetProject") == null) {
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

    	// primary Key column
    	IntrospectedColumn primaryKeyColumn = null;
    	if(introspectedTable.hasPrimaryKeyColumns()) {
    		primaryKeyColumn = introspectedTable.getPrimaryKeyColumns().get(0);
    	}
    	
    	// Controller 클래스 생성
    	TopLevelClass controllerClass = generateControllerClass(tableName, dtoType, primaryKeyColumn);
    	
    	// Java 파일 생성
    	GeneratedJavaFile controllerFile = new GeneratedJavaFile(controllerClass, properties.getProperty("targetProject"), context.getJavaFormatter());
    	
		fileList.add(controllerFile);
		
		return fileList;
	}
	
	private TopLevelClass generateControllerClass(String tableName, String dtoType, IntrospectedColumn primaryKeyColumn) {
		String className = StringUtil.toPascalCase(tableName);
		FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType("com.example.service." + className + "Service");
		FullyQualifiedJavaType controllerType = new FullyQualifiedJavaType("com.example.controller." + className + "Controller");

        // 클래스 생성
        TopLevelClass controllerClass = new TopLevelClass(controllerType);
        controllerClass.setVisibility(JavaVisibility.PUBLIC);
        controllerClass.addImportedType(dtoType);
        controllerClass.addImportedType(serviceType);
        controllerClass.addImportedType("java.util.*");
        controllerClass.addImportedType("org.springframework.http.ResponseEntity");
        controllerClass.addImportedType("org.springframework.beans.factory.annotation.*");
        controllerClass.addImportedType("org.springframework.web.bind.annotation.*");
        
        // @Controller 어노테이션 추가
        controllerClass.addAnnotation("@RestController");
        // @RequestMapping 어노테이션 추가
        controllerClass.addAnnotation("@RequestMapping(\"/" + tableName +"\")");
        
        // Service 필드 추가
        Field serviceField = new Field("service", serviceType);
        serviceField.setVisibility(JavaVisibility.PRIVATE);
        serviceField.addAnnotation("@Autowired");
        controllerClass.addField(serviceField);
        
		// 메서드 추가: create
		Method createMethod = new Method("create");
		createMethod.setVisibility(JavaVisibility.PUBLIC);
		createMethod.addAnnotation("@PostMapping(\"/create\")");
		createMethod.setReturnType(new FullyQualifiedJavaType("ResponseEntity<String>"));
		createMethod.addParameter(createAnnotatedParameter("@RequestBody", new FullyQualifiedJavaType(dtoType), "dto"));
		createMethod.addBodyLine("int result = service.create(dto);");
		createMethod.addBodyLine("if (result > 0) return ResponseEntity.ok(\"success create\");");
		createMethod.addBodyLine("else return ResponseEntity.status(400).body(\"fail create\");");
		controllerClass.addMethod(createMethod);

		// 메서드 추가: createSelective
		Method createSelectiveMethod = new Method("createSelective");
		createSelectiveMethod.setVisibility(JavaVisibility.PUBLIC);
		createSelectiveMethod.addAnnotation("@PostMapping(\"/create-selective\")");
		createSelectiveMethod.setReturnType(new FullyQualifiedJavaType("ResponseEntity<String>"));
		createSelectiveMethod.addParameter(createAnnotatedParameter("@RequestBody", new FullyQualifiedJavaType(dtoType), "dto"));
		createSelectiveMethod.addBodyLine("int result = service.createSelective(dto);");
		createSelectiveMethod.addBodyLine("if (result > 0) return ResponseEntity.ok(\"success create selective\");");
		createSelectiveMethod.addBodyLine("else return ResponseEntity.status(400).body(\"fail create selective\");");
		controllerClass.addMethod(createSelectiveMethod);

		// 메서드 추가: find
		Method findMethod = new Method("find" + className);
		findMethod.setVisibility(JavaVisibility.PUBLIC);
		findMethod.addAnnotation("@GetMapping(\"/find\")");
		findMethod.setReturnType(new FullyQualifiedJavaType("ResponseEntity<Map<String, Object>>"));
		findMethod.addParameter(createAnnotatedParameter("@RequestParam(defaultValue = \"1\")", FullyQualifiedJavaType.getIntInstance(), "page"));
		findMethod.addParameter(createAnnotatedParameter("@RequestParam(defaultValue = \"10\")", FullyQualifiedJavaType.getIntInstance(), "size"));
		findMethod.addParameter(createAnnotatedParameter("@ModelAttribute", new FullyQualifiedJavaType(dtoType), "dto"));
		findMethod.addBodyLine("List<" + dtoType + ">" + "list = service.find" + className + "(page, size, dto);");
		findMethod.addBodyLine("int totalDtos = service.count" + className + "(dto);");
		findMethod.addBodyLine("int totalPages = totalDtos == 0 ? 0 : (int) Math.ceil((double) totalDtos / size);");
		findMethod.addBodyLine("Map<String, Object> response = new HashMap<>();");
		findMethod.addBodyLine("response.put(\"list\", list);");
		findMethod.addBodyLine("response.put(\"currentPage\", page);");
		findMethod.addBodyLine("response.put(\"totalPages\", totalPages);");
		findMethod.addBodyLine("return ResponseEntity.ok(response);");
		controllerClass.addMethod(findMethod);
     		
        // 기본키가 존재하면 primaryKey 관련 메서드 생성
        if(primaryKeyColumn != null) {
        	FullyQualifiedJavaType pkJavaType = primaryKeyColumn.getFullyQualifiedJavaType();
        	String pkName = primaryKeyColumn.getJavaProperty();
        	String pascalPkName =  StringUtil.toPascalCase(pkName);
        	
        	// 메서드 추가: deleteByPrimaryKey
        	Method deleteMethod = new Method("deleteBy" + pascalPkName);
        	deleteMethod.setVisibility(JavaVisibility.PUBLIC);
        	deleteMethod.addAnnotation("@DeleteMapping(\"/delete/{" + pkName + "}\")");
        	deleteMethod.setReturnType(new FullyQualifiedJavaType("ResponseEntity<String>"));
        	deleteMethod.addParameter(createAnnotatedParameter("@PathVariable", pkJavaType, pkName));
        	deleteMethod.addBodyLine("int result = service.deleteBy" + pascalPkName + "(" + pkName + ");");
        	deleteMethod.addBodyLine("if (result > 0) return ResponseEntity.ok(\"success delete\");");
        	deleteMethod.addBodyLine("else return ResponseEntity.status(404).body(\"fail delete\");");
        	controllerClass.addMethod(deleteMethod);

        	// 메서드 추가: updateByPrimaryKeySelective
        	Method updateSelectiveMethod = new Method("updateBy" + pascalPkName + "Selective");
        	updateSelectiveMethod.setVisibility(JavaVisibility.PUBLIC);
        	updateSelectiveMethod.addAnnotation("@PatchMapping(\"/update-selective/{" + pkName + "}\")");
        	updateSelectiveMethod.setReturnType(new FullyQualifiedJavaType("ResponseEntity<String>"));
        	updateSelectiveMethod.addParameter(createAnnotatedParameter("@PathVariable", pkJavaType, pkName));
        	updateSelectiveMethod.addParameter(createAnnotatedParameter("@RequestBody", new FullyQualifiedJavaType(dtoType), "dto"));
        	updateSelectiveMethod.addBodyLine("dto.set" + pascalPkName + "(" + pkName + ");");
        	updateSelectiveMethod.addBodyLine("int result = service.updateBy" + pascalPkName + "Selective(dto);");
        	updateSelectiveMethod.addBodyLine("if (result > 0) return ResponseEntity.ok(\"success update selective\");");
        	updateSelectiveMethod.addBodyLine("else return ResponseEntity.status(400).body(\"fail update selective\");");
        	controllerClass.addMethod(updateSelectiveMethod);

        	// 메서드 추가: updateByPrimaryKey
        	Method updateMethod = new Method("updateBy" + pascalPkName);
        	updateMethod.setVisibility(JavaVisibility.PUBLIC);
        	updateMethod.addAnnotation("@PutMapping(\"/update/{" + pkName + "}\")");
        	updateMethod.setReturnType(new FullyQualifiedJavaType("ResponseEntity<String>"));
        	updateMethod.addParameter(createAnnotatedParameter("@PathVariable", pkJavaType, pkName));
        	updateMethod.addParameter(createAnnotatedParameter("@RequestBody", new FullyQualifiedJavaType(dtoType), "dto"));
        	updateMethod.addBodyLine("dto.set" + pascalPkName + "(" + pkName + ");");
        	updateMethod.addBodyLine("int result = service.updateBy" + pascalPkName + "(dto);");
        	updateMethod.addBodyLine("if (result > 0) return ResponseEntity.ok(\"success update\");");
        	updateMethod.addBodyLine("else return ResponseEntity.status(400).body(\"fail update\");");
        	controllerClass.addMethod(updateMethod);
        }
        
        return controllerClass;
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
