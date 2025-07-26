package org.spring.shopagent.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
public class DynamicDataSourceConfig {

    private DataSource dataSource;

    private SqlSessionFactory sqlSessionFactory;

    public DataSource initialize(String url, String username, String password, ApplicationContext ctx) throws Exception {
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
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableContext.getBeanFactory();

        // 기존 빈 제거
        if (beanFactory.containsSingleton("dataSource")) {
            beanFactory.destroySingleton("dataSource");
        }
        if (beanFactory.containsSingleton("sqlSessionFactory")) {
            beanFactory.destroySingleton("sqlSessionFactory");
        }

        // 새로 등록
        beanFactory.registerSingleton("dataSource", dataSource);
        beanFactory.registerSingleton("sqlSessionFactory", sqlSessionFactory);

        return dataSource;
    }


    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
