package neo.spider.admin.flow.config;

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
        basePackages = "neo.spider.admin.flow.mapper",
        sqlSessionFactoryRef = "flowSqlSessionFactory"
)
@RequiredArgsConstructor
public class FlowDbConfig {

/*
 * 다른 데이터베이스 사용 시 DataSource 설정 필요
 */
//    private final Environment environment;
//
//    @Bean(name = "flowDataSource")
//    @ConfigurationProperties(prefix = "spider.admin.datasource-flow") // flow DB 관련 설정
//    public DataSource flowDataSource() {
//        return DataSourceBuilder.create().url(environment.getProperty("spider.admin.datasource-flow.url")).build();
//    }

    @Bean(name = "flowSqlSessionFactory")
    public SqlSessionFactory flowSqlSessionFactory(
            @Qualifier("commonDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTypeAliasesPackage("neo.spider.admin.flow.dto");
        factoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:mappers/flow/*.xml")
        );
        org.apache.ibatis.session.Configuration mybatisConfig = new org.apache.ibatis.session.Configuration();
        mybatisConfig.setMapUnderscoreToCamelCase(true); // underscore to camelCase
        factoryBean.setConfiguration(mybatisConfig);

        return factoryBean.getObject();
    }

    @Bean(name = "flowSqlSessionTemplate")
    public SqlSessionTemplate flowSqlSessionTemplate(
            @Qualifier("flowSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
