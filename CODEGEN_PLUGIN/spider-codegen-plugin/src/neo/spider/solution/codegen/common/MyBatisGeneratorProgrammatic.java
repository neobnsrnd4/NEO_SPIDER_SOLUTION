package neo.spider.solution.codegen.common;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;

import neo.spider.solution.codegen.util.StringUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// mybatis-generator-core 라이브러리: MyBatis에서 제공하는 코드 생성기
public class MyBatisGeneratorProgrammatic {

	public static boolean execute(Shell shell, String url, String userId, String password, String targetPath, String tableName) {
		try {
			// Warnings list to capture any warnings during the generation process
			List<String> warnings = new ArrayList<>();

			// Overwrite existing files if necessary
			boolean overwrite = true;

			// Create the MyBatis Generator Configuration
			Configuration config = new Configuration();
			
			/*
			 * Context 설정
			 */
			// ModelType.FLAT: 기본 키 분리 안함
			// ModelType.HIERARCHICAL: 기본 키 분리함 (복합 키 가 있는 경우 유용)
			// ModelType.CONDITIONAL: 복합 키가 있는 경우 HIERARCHICAL 방식, 없는 경우 FLAT 방식으로 자동 결정
			Context context = new Context(ModelType.CONDITIONAL);
			context.setId("MyBatis3Context");
			// MyBatis3: Example 클래스 (동적 쿼리를 위한 도우미 클래스)도 추가
			// MyBatis3Simple: deleteByPrimaryKey, insert, insertSelective, selectByPrimaryKey, updateByPrimaryKey, updateByPrimaryKeySelective ...
			context.setTargetRuntime("MyBatis3"); // Use "MyBatis3Simple" if you want simpler output
			config.addContext(context);

			/*
			 * JDBC 연결 설정
			 */
			JDBCConnectionConfiguration jdbcConfig = new JDBCConnectionConfiguration();
			jdbcConfig.setDriverClass("com.mysql.cj.jdbc.Driver");
			jdbcConfig.setConnectionURL(url);
			jdbcConfig.setUserId(userId);
			jdbcConfig.setPassword(password);
			context.setJdbcConnectionConfiguration(jdbcConfig);

			Path javaPath = Paths.get(targetPath).resolve("src/main/java").toAbsolutePath();
			Path resources = Paths.get(targetPath).resolve("src/main/resources").toAbsolutePath();

			if (Files.notExists(javaPath)) {
				// Create the directory and all necessary parent directories
				Files.createDirectories(javaPath);
				System.out.println("Directory created at: " + javaPath.toAbsolutePath());
			}
			if (Files.notExists(resources)) {
				// Create the directory and all necessary parent directories
				Files.createDirectories(resources);
				System.out.println("Directory created at: " + resources.toAbsolutePath());
			}

			/*
			 * Java Model(DTO) 생성 설정
			 */
			JavaModelGeneratorConfiguration modelConfig = new JavaModelGeneratorConfiguration();
			modelConfig.setTargetPackage("com.example.dto"); // Output package for model classes
			modelConfig.setTargetProject(javaPath.toString()); // Output directory
			context.setJavaModelGeneratorConfiguration(modelConfig);

			/*
			 * Mapper Interface 생성 설정
			 */
			JavaClientGeneratorConfiguration clientConfig = new JavaClientGeneratorConfiguration();
			clientConfig.setTargetPackage("com.example.mapper"); // Output package for mapper interfaces
			clientConfig.setTargetProject(javaPath.toString()); // Output directory
			clientConfig.setConfigurationType("XMLMAPPER"); // Use XML-based mapper
			context.setJavaClientGeneratorConfiguration(clientConfig);
			
			/*
			 * Mapper XML 생성 설정
			 */
			SqlMapGeneratorConfiguration sqlMapConfig = new SqlMapGeneratorConfiguration();
			sqlMapConfig.setTargetPackage("mappers"); // Output package for XML files
			sqlMapConfig.setTargetProject(resources.toString()); // Output directory
			context.setSqlMapGeneratorConfiguration(sqlMapConfig);

			/*
			 * 코드 생성할 테이블 설정
			 */
			TableConfiguration tableConfig = new TableConfiguration(context);
			tableConfig.setTableName(tableName); // Table name in the database
			tableConfig.setDomainObjectName(StringUtil.toPascalCase(tableName) + "DTO"); // DTO 클래스 이름 설정 -> customFileNamePlugin으로 mapper 이름 설정 필요
			// dynamic query와 관련된 코드 생성 X -> true 설정 시 exampel Model 클래스 생성
			tableConfig.setSelectByExampleStatementEnabled(false);
			tableConfig.setDeleteByExampleStatementEnabled(false);
			tableConfig.setCountByExampleStatementEnabled(false);
			tableConfig.setUpdateByExampleStatementEnabled(false);
			context.addTableConfiguration(tableConfig);
			
			// Additional Plugins (Optional)
			// 페이징처리가 가능한 find 메소드와 count 메소드가 생성되도록 하는 플러그인
			PluginConfiguration paginationPlugin = new PluginConfiguration();
			paginationPlugin.setConfigurationType("neo.spider.solution.codegen.plugins.PaginationPlugin");
			context.addPluginConfiguration(paginationPlugin);
			
			// DTO 클래스에 toString 메소드가 생성되도록 하는 플러그인
			PluginConfiguration toStringPlugin = new PluginConfiguration();
			toStringPlugin.setConfigurationType("org.mybatis.generator.plugins.ToStringPlugin");
			context.addPluginConfiguration(toStringPlugin);
			
			// 웹 표준에 맞춰 파일명이 생성되도록 하는 플러그인
			PluginConfiguration fileNamePlugin = new PluginConfiguration();
			fileNamePlugin.setConfigurationType("neo.spider.solution.codegen.plugins.CustomFileNamePlugin");
			context.addPluginConfiguration(fileNamePlugin);
			
			// Service 코드가 생성되도록 하는 플러그인
			PluginConfiguration servicePlugin = new PluginConfiguration();
			servicePlugin.setConfigurationType("neo.spider.solution.codegen.plugins.ServiceGeneratorPlugin");
			servicePlugin.addProperty("targetProject", javaPath.toString());
			context.addPluginConfiguration(servicePlugin);
			
			// Controller 코드가 생성되도록 하는 플러그인
			PluginConfiguration controllerPlugin = new PluginConfiguration();
			controllerPlugin.setConfigurationType("neo.spider.solution.codegen.plugins.ControllerGeneratorPlugin");
			controllerPlugin.addProperty("targetProject", javaPath.toString());
			context.addPluginConfiguration(controllerPlugin);
			
			// 자동으로 생성되는 주석을 생기지 않도록 설정
			CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
			commentGeneratorConfiguration.addProperty("suppressAllComments", "true"); // 모든 주석 비활성화
			context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

			// Run the MyBatis Generator
			DefaultShellCallback callback = new DefaultShellCallback(overwrite);
			MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
			myBatisGenerator.generate(null);
			
			// Print warnings, if any
			for (String warning : warnings) {
				System.out.println(warning);
			}
			
			MessageDialog.openInformation(shell, "Code Generated", "Code generated successfully at: " + Paths.get(targetPath).toAbsolutePath() + "\nFor table: " + tableName);

			return true;
			
		} catch (Exception e) {
			MessageDialog.openError(shell, "Error", e.getMessage());
			return false;
		}
	}
	
}
