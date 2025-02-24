package neo.spider.demo.E2E.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import neo.spider.demo.E2E.jpa.ProjectStatementInspector;
import neo.spider.demo.E2E.mybatis.ProjectMyBatisInterceptor;
import neo.spider.demo.E2E.properties.DBProperties;
import neo.spider.solution.E2E.CompositeStatementInspector;
import neo.spider.solution.E2E.JPALoggingInspector;
import neo.spider.solution.E2E.MyBatisInterceptor;

@Configuration
@EnableJpaRepositories(
		basePackages = "neo.spider.demo.E2E.repository"
		,entityManagerFactoryRef = "dataEntityManager"
		, transactionManagerRef = "dataTransactionsManager")
@RequiredArgsConstructor
public class DBConfig {
	private final DBProperties dbProperties;
//	private final JPALoggingInspector inspector;
	private final CompositeStatementInspector compositeStatementInspector;
	
	@Bean(name = "dataDataSource")
	@ConfigurationProperties(prefix = "spring.datasource-data")
	public DataSource dataDataSource() {    	
		return DataSourceBuilder.create()
				.url(dbProperties.getDataUrl())
				.build();
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception{
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		
		// Mybatis interceptor 등록
		org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
//		configuration.addInterceptor(new MybatisLoggingInterceptor());
		
//		configuration.addInterceptor(new MyBatisInterceptor());
		
		Interceptor[] interceptors = new Interceptor[] {
				new ProjectMyBatisInterceptor(),
				new MyBatisInterceptor()
		};
		
		factory.setDataSource(dataDataSource());
		factory.setMapperLocations(new PathMatchingResourcePatternResolver()
				.getResources("classpath:mappers/*.xml"));
		
//		factory.setConfiguration(configuration); // Mybatis interceptor 등록
		
		factory.addPlugins(interceptors);
		
		return factory.getObject();
		
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean dataEntityManager() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		
		em.setDataSource(dataDataSource());
		em.setPackagesToScan(new String[] {"neo.spider.demo.E2E.entity"});
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", "update");
//		properties.put("hibernate.show_sql", "true");
		properties.put("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect");
		
		compositeStatementInspector.addInspectorAtBeginning(new ProjectStatementInspector());
		properties.put("hibernate.session_factory.statement_inspector", compositeStatementInspector);
	    
		em.setJpaPropertyMap(properties);
		
		
	
		
		return em;
	}
	
	@Bean
	public PlatformTransactionManager dataTransactionsManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(dataEntityManager().getObject());
		return transactionManager;
	}
}
