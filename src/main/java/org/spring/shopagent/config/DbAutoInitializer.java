package org.spring.shopagent.config;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.spring.shopagent.mapper.ShopMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class DbAutoInitializer implements ApplicationRunner {

    private final ApplicationContext ctx;
    private final DynamicDataSourceConfig dynamicDataSourceConfig;
    private final DynamicMapperRegistrar dynamicMapperRegistrar;

    public static boolean dbConnected = false;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        File file = new File("db-config.properties");

        if (file.exists()) {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(file)) {
                props.load(in);
            }

            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            try {
                DataSource dataSource = dynamicDataSourceConfig.initialize(url, username, password, ctx);
                dbConnected = true;
                System.out.println("DB 자동 연결 성공");

                // 2. SqlSessionFactory 생성
                SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
                factoryBean.setDataSource(dataSource);
                factoryBean.setMapperLocations(
                        new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/**/*.xml"));
                SqlSessionFactory sqlSessionFactory = factoryBean.getObject();
                dynamicMapperRegistrar.registerMappers("org.spring.shopagent.mapper", sqlSessionFactory, ctx);
                String[] beanNames = ctx.getBeanDefinitionNames();
                System.out.println("등록된 Bean:");
                Arrays.stream(beanNames)
                        .filter(name -> name.contains("shop") || name.contains("Mapper"))
                        .forEach(System.out::println);


                System.out.println("Mapper 등록 완료");

                ShopMapper shopMapper = ctx.getBean(ShopMapper.class);
                System.out.println("select Result: " + shopMapper.selectNow());
            } catch (Exception e) {
                dbConnected = false;
                System.err.println("DB 연결 실패: " + e.getMessage());
                // 필요시 설정 파일 삭제도 가능
                // file.delete();
            }

        } else {
            System.out.println("DB 설정 파일 없음. 웹 UI에서 설정하세요.");
        }
    }


}

