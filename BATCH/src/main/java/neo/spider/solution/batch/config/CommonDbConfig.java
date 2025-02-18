package neo.spider.solution.batch.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableJpaRepositories(basePackages = "neo.spider.solution.batch.repository", entityManagerFactoryRef = "dataEntityManager", transactionManagerRef = "dataTransactionsManager")
@RequiredArgsConstructor
@MapperScan(basePackages = "neo.spider.solution.batch.mapper", sqlSessionFactoryRef = "mysqlSqlSessionFactory")
public class CommonDbConfig {

	private final DBProperties dbProperties;

	@Bean(name = "commonDataSource")
	@ConfigurationProperties(prefix = "spider.batch.datasource-common")
	public DataSource dataDataSource() {
		return DataSourceBuilder.create().url(dbProperties.getCommonUrl()).build();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean dataEntityManager() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

		em.setDataSource(dataDataSource());
		em.setPackagesToScan(new String[] { "neo.spider.solution.batch.entity" });
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

		HashMap<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", "update");
		properties.put("hibernate.show_sql", "true");
		properties.put("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect");
		em.setJpaPropertyMap(properties);

		return em;
	}

	@Bean
	public PlatformTransactionManager dataTransactionsManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(dataEntityManager().getObject());
		return transactionManager;
	}

	@Primary
	@Bean(name = "mysqlSqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();

		factory.setDataSource(dataDataSource());
//		factory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mappers/*.xml"));

		return factory.getObject();

	}

	@Primary
	@Bean(name = "sqlSessionTemplate")
	public SqlSessionTemplate sqlSessionTemplate() throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory());
	}

}
