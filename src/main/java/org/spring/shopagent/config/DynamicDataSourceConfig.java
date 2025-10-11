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
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DynamicDataSourceConfig {

    private DataSource dataSource;

    private SqlSessionFactory sqlSessionFactory;

    public DataSource initialize(String url, String username, String password, ApplicationContext ctx) throws Exception {
        try {
            System.out.println("=== DynamicDataSourceConfig.initialize START ===");

            System.out.println("Creating HikariConfig...");

            // JDBC 드라이버 로드 테스트
            try {
                System.out.println("Testing JDBC driver loading...");
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("JDBC driver loaded successfully!");
            } catch (ClassNotFoundException e) {
                System.err.println("FATAL: MySQL JDBC Driver not found!");
                System.err.println("This is likely a GraalVM Native Image configuration issue.");
                throw new RuntimeException("MySQL JDBC Driver not available", e);
            }

            // DriverManager를 사용한 직접 연결 테스트
            System.out.println("=== Testing Direct JDBC Connection ===");
            try {
                System.out.println("Attempting DriverManager.getConnection()...");
                java.sql.Connection testConn = java.sql.DriverManager.getConnection(url, username, password);
                if (testConn == null) {
                    System.err.println("ERROR: DriverManager.getConnection() returned NULL!");
                } else {
                    System.out.println("SUCCESS: Direct JDBC connection established!");
                    System.out.println("Connection class: " + testConn.getClass().getName());
                    System.out.println("Connection valid: " + testConn.isValid(5));
                    testConn.close();
                    System.out.println("Test connection closed successfully");
                }
            } catch (Exception e) {
                System.err.println("ERROR: Direct JDBC connection failed!");
                System.err.println("Error type: " + e.getClass().getName());
                System.err.println("Error message: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println("=== Direct JDBC Connection Test Complete ===");

            // HikariCP는 GraalVM Native Image에서 문제가 있으므로 DriverManagerDataSource 사용
            System.out.println("Creating DriverManagerDataSource (bypassing HikariCP due to Native Image compatibility)...");
            DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
            driverManagerDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            driverManagerDataSource.setUrl(url);
            driverManagerDataSource.setUsername(username);
            driverManagerDataSource.setPassword(password);

            System.out.println("DriverManagerDataSource details:");
            System.out.println("  - JDBC URL: " + url);
            System.out.println("  - Username: " + username);
            System.out.println("  - Driver: com.mysql.cj.jdbc.Driver");

            DataSource dataSource = driverManagerDataSource;
            System.out.println("DriverManagerDataSource created successfully!");

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
