package org.spring.shopagent.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
public class DynamicDataSourceConfig {

    private DataSource dataSource;

    private SqlSessionFactory sqlSessionFactory;

    public void initialize(String url, String username, String password, ApplicationContext ctx) throws Exception {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        DataSource dataSource = new HikariDataSource(config);

        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/**/*.xml"));
        SqlSessionFactory sqlSessionFactory = sessionFactoryBean.getObject();

        ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) ctx;
        configurableContext.getBeanFactory().registerSingleton("dataSource", dataSource);
        configurableContext.getBeanFactory().registerSingleton("sqlSessionFactory", sqlSessionFactory);
    }


    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
