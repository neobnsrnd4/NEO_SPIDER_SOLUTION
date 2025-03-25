package neo.spider.admin.common.config;

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
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@MapperScan(
    basePackages = "neo.spider.admin.common.mapper",
    sqlSessionFactoryRef = "commonSqlSessionFactory"
)
@RequiredArgsConstructor
public class CommonDbConfig {
	
	private final Environment environment;

	@Bean(name = "commonDataSource")
	@ConfigurationProperties(prefix = "spider.admin.datasource-common")
	public DataSource commonDataSource() {
	    return DataSourceBuilder
	            .create()
	            .url(environment.getProperty("spider.admin.datasource-common.url"))
	            .build();
	}
	
	@Bean(name = "commonSqlSessionFactory")
    public SqlSessionFactory commonSqlSessionFactory(
            @Qualifier("commonDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTypeAliasesPackage("neo.spider.admin.common.dto, neo.spider.admin.e2e.dto");
        factoryBean.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath:mappers/common/*.xml")
        );
        org.apache.ibatis.session.Configuration mybatisConfig = new org.apache.ibatis.session.Configuration();
        mybatisConfig.setMapUnderscoreToCamelCase(true); // underscore to camelCase
        factoryBean.setConfiguration(mybatisConfig);

        return factoryBean.getObject();
    }

    @Bean(name = "commonSqlSessionTemplate")
    public SqlSessionTemplate commonSqlSessionTemplate(
            @Qualifier("commonSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
