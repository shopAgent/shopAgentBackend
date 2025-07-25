package org.spring.shopagent.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class DbAutoInitializer implements ApplicationRunner {

    private final ApplicationContext ctx;
    private final DynamicDataSourceConfig dynamicDataSourceConfig;

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
                dynamicDataSourceConfig.initialize(url, username, password, ctx);
                dbConnected = true;
                System.out.println("DB 자동 연결 성공");
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

