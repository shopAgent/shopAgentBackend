package org.spring.shopagent.config;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.spring.shopagent.common.DynamicDatabaseService;
import org.spring.shopagent.mapper.ShopMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class DbAutoInitializer implements ApplicationRunner {

    private final DynamicDatabaseService dynamicDatabaseService;

    public static boolean dbConnected = false;

    @Override
    public void run(ApplicationArguments args) {
        File file = new File("db-config.yml");

        if (!file.exists()) {
            System.out.println("DB 설정 파일 없음. 웹 UI에서 설정하세요.");
            return;
        }

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);
        } catch (IOException e) {
            System.err.println("설정 파일 읽기 실패: " + e.getMessage());
            return;
        }

        String url = props.getProperty("db.url");
        String username = props.getProperty("db.username");
        String password = props.getProperty("db.password");

        dbConnected = dynamicDatabaseService.initializeDatabase(url, username, password, true);
    }

    public static void isDbConnectedThrow() {
        if (!dbConnected)
            throw new RuntimeException("DB 연결이 실패했습니다. 설정을 확인하세요.");
    }


}

