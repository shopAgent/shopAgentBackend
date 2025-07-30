package org.spring.shopagent.common;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.spring.shopagent.config.DynamicDataSourceConfig;
import org.spring.shopagent.config.DynamicMapperRegistrar;
import org.spring.shopagent.exception.CustomException;
import org.spring.shopagent.exception.ErrorType;
import org.spring.shopagent.mapper.ShopMapper;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DynamicDatabaseService {

    private final DynamicDataSourceConfig dynamicDataSourceConfig;
    private final DynamicMapperRegistrar dynamicMapperRegistrar;
    private final ApplicationContext ctx;

        public boolean initializeDatabase(String url, String username, String password, boolean testQuery) {
            try {
                DataSource dataSource = dynamicDataSourceConfig.initialize(url, username, password, ctx);

                SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
                factoryBean.setDataSource(dataSource);
                factoryBean.setMapperLocations(
                        new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/**/*.xml"));
                SqlSessionFactory sqlSessionFactory = factoryBean.getObject();

                dynamicMapperRegistrar.registerMappers("org.spring.shopagent.mapper", sqlSessionFactory, ctx);
                System.out.println("Mapper 등록 완료");

                if (testQuery) {
                    ShopMapper shopMapper = ctx.getBean(ShopMapper.class);
                    System.out.println("Test Query 결과: " + shopMapper.selectNow());
                }

                return true;

            } catch (Exception e) {
                throw new CustomException(ErrorType.DB_CONNECTION_ERROR, e.getMessage());
            }
        }

    public void destroy(List<String> mapperBeanNames) {
        ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) ctx;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableContext.getBeanFactory();

        if (beanFactory.containsSingleton("dataSource")) {
            DataSource ds = (DataSource) beanFactory.getBean("dataSource");
            if (ds instanceof HikariDataSource hikari) {
                hikari.close();
                System.out.println("HikariDataSource 종료됨");
            }
            beanFactory.destroySingleton("dataSource");
        }

        if (beanFactory.containsSingleton("sqlSessionFactory")) {
            beanFactory.destroySingleton("sqlSessionFactory");
        }

        for (String beanName : mapperBeanNames) {
            if (beanFactory.containsSingleton(beanName)) {
                beanFactory.destroySingleton(beanName);
            }
        }

        System.out.println("동적 DB 리소스 제거 완료");
    }

    public String createDBJdbcUrl(String databaseType, String dbName, String url, Integer dbPort) {

        return switch (databaseType) {
            case "MySQL" -> url = "jdbc:mysql://" + url + ":" + dbPort + "/" + dbName;
            case "MSSQL" -> url = "jdbc:sqlserver://" + url + ":" + dbPort + ";databaseName=" + dbName;
            case "PostgreSQL" -> url = "jdbc:postgresql://" + url + ":" + dbPort + "/" + dbName;
            case "SQLite" -> url = "jdbc:sqlite:" + dbName;
            default -> throw new CustomException(ErrorType.DB_CONNECTION_ERROR);
        };
    }
}
