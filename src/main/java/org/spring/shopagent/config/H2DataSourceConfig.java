package org.spring.shopagent.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
@RequiredArgsConstructor
@MapperScan(basePackages = "org.spring.shopagent.h2mapper", sqlSessionFactoryRef = "h2SqlSessionFactory")
public class H2DataSourceConfig {

    private final H2DataSourceProperties h2Props;


    @Bean(name = "h2DataSource")
    public DataSource h2DataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(h2Props.getUrl());
        config.setUsername(h2Props.getUsername());
        config.setPassword(h2Props.getPassword());
        config.setDriverClassName(h2Props.getDriverClassName());
        return new HikariDataSource(config);
    }

    @Bean(name = "h2SqlSessionFactory")
    public SqlSessionFactory h2SqlSessionFactory(@Qualifier("h2DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:/h2mapper/**/*.xml"));

        org.apache.ibatis.session.Configuration mybatisConfig = new org.apache.ibatis.session.Configuration();
        mybatisConfig.setMapUnderscoreToCamelCase(true);

        bean.setConfiguration(mybatisConfig);

        return bean.getObject();
    }

    @Bean(name = "h2SqlSessionTemplate")
    public SqlSessionTemplate h2SqlSessionTemplate(@Qualifier("h2SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}