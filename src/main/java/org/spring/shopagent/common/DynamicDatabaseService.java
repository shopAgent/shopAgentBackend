package org.spring.shopagent.common;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.spring.shopagent.config.DynamicDataSourceConfig;
import org.spring.shopagent.config.DynamicMapperRegistrar;
import org.spring.shopagent.mapper.ShopMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

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
            System.err.println("DB 초기화 실패: " + e.getMessage());
            return false;
        }
    }
}
