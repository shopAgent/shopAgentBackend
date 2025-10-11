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
        try {
            System.out.println("=== DynamicDataSourceConfig.initialize START ===");

            System.out.println("Creating HikariConfig...");
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password != null ? "***" : "null");
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            System.out.println("HikariConfig details:");
            System.out.println("  - JDBC URL: " + url);
            System.out.println("  - Username: " + username);
            System.out.println("  - Driver: com.mysql.cj.jdbc.Driver");

            System.out.println("Creating HikariDataSource (this will test the connection)...");
            config.setPassword(password); // Restore actual password
            DataSource dataSource = new HikariDataSource(config);
            System.out.println("HikariDataSource created successfully!");

            System.out.println("Creating SqlSessionFactory...");
            SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
            sessionFactoryBean.setDataSource(dataSource);
            sessionFactoryBean.setMapperLocations(
                    new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/**/*.xml"));
            SqlSessionFactory sqlSessionFactory = sessionFactoryBean.getObject();
            System.out.println("SqlSessionFactory created successfully!");

            ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) ctx;
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableContext.getBeanFactory();

            // 기존 빈 제거
            System.out.println("Removing existing beans if present...");
            if (beanFactory.containsSingleton("dataSource")) {
                beanFactory.destroySingleton("dataSource");
                System.out.println("  - Removed old dataSource bean");
            }
            if (beanFactory.containsSingleton("sqlSessionFactory")) {
                beanFactory.destroySingleton("sqlSessionFactory");
                System.out.println("  - Removed old sqlSessionFactory bean");
            }

            // 새로 등록
            System.out.println("Registering new beans...");
            beanFactory.registerSingleton("dataSource", dataSource);
            beanFactory.registerSingleton("sqlSessionFactory", sqlSessionFactory);
            System.out.println("Beans registered successfully!");

            System.out.println("=== DynamicDataSourceConfig.initialize SUCCESS ===");
            return dataSource;

        } catch (Exception e) {
            System.err.println("=== DynamicDataSourceConfig.initialize FAILED ===");
            System.err.println("Exception Type: " + e.getClass().getName());
            System.err.println("Exception Message: " + e.getMessage());

            if (e.getCause() != null) {
                System.err.println("Root Cause Type: " + e.getCause().getClass().getName());
                System.err.println("Root Cause Message: " + e.getCause().getMessage());
            }

            System.err.println("Full stack trace:");
            e.printStackTrace();

            throw e;
        }
    }


    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
