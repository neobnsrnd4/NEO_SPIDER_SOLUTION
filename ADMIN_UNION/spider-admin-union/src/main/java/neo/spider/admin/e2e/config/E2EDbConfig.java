package neo.spider.admin.e2e.config;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(
        basePackages = {"neo.spider.admin.e2e.mapper"},
        sqlSessionFactoryRef = "e2eSqlSessionFactory"
)
@RequiredArgsConstructor
public class E2EDbConfig {

/*
 * 다른 데이터베이스 사용 시 DataSource 설정 필요
 */
//    private final Environment environment;
//
//    @Bean(name = "e2eDataSource")
//    @ConfigurationProperties(prefix = "spider.admin.datasource-e2e") // e2e DB 관련 설정
//    public DataSource e2eDataSource() {
//        return DataSourceBuilder.create().url(environment.getProperty("spider.admin.datasource-e2e.url")).build();
//    }

    @Bean(name = "e2eSqlSessionFactory")
    public SqlSessionFactory e2eSqlSessionFactory(
            @Qualifier("commonDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTypeAliasesPackage("neo.spider.admin.e2e.dto");
        factoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:mappers/e2e/*.xml")
        );
        org.apache.ibatis.session.Configuration mybatisConfig = new org.apache.ibatis.session.Configuration();
        mybatisConfig.setMapUnderscoreToCamelCase(true); // underscore to camelCase
        factoryBean.setConfiguration(mybatisConfig);

        return factoryBean.getObject();
    }

    @Bean(name = "e2eSqlSessionTemplate")
    public SqlSessionTemplate e2eSqlSessionTemplate(
            @Qualifier("e2eSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
