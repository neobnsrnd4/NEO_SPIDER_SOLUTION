package neo.spider.solution.batch.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SpiderDbConfig {

	private final DBProperties dbProperties;

	@Bean
	@ConfigurationProperties(prefix = "spider.batch.datasource-spiderdb")
	public DataSource spiderDataSource() {
		return DataSourceBuilder.create().url(dbProperties.getSpiderUrl()).build();
	}

	@Bean
	public JdbcTemplate spiderTemplate() {
		return new JdbcTemplate(spiderDataSource());

	}

}
