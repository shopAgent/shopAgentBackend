package org.spring.shopagent.config;

import lombok.RequiredArgsConstructor;
import org.spring.shopagent.common.DynamicDatabaseService;
import org.spring.shopagent.exception.CustomException;
import org.spring.shopagent.exception.ErrorType;
import org.spring.shopagent.h2mapper.H2Mapper;
import org.spring.shopagent.info.dto.DatabaseInfoDTO;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbAutoInitializer implements ApplicationRunner {

    private final DynamicDatabaseService dynamicDatabaseService;

    private final H2Mapper h2Mapper;

    public static boolean dbConnected = false;

    @Override
    public void run(ApplicationArguments args) {
        try {
            DatabaseInfoDTO dbInfo = h2Mapper.selectDatabaseInfo();

            if (dbInfo == null || dbInfo.getUrl().isEmpty() || dbInfo.getDbUserName().isEmpty() || dbInfo.getDbPassword().isEmpty()) {
                throw new CustomException(ErrorType.DB_CONFIG_NOT_FOUND);
            }

            String url = dynamicDatabaseService.createDBJdbcUrl(
                    dbInfo.getDatabaseType(),
                    dbInfo.getDbName(),
                    dbInfo.getUrl(),
                    dbInfo.getDbPort());

            dbConnected = dynamicDatabaseService.initializeDatabase(url, dbInfo.getDbUserName(), dbInfo.getDbPassword(), true);
        } catch (CustomException e) {

        }
    }

    public static void isDbConnectedThrow() {
        if (!dbConnected)
            throw new RuntimeException("DB 연결이 실패했습니다. 설정을 확인하세요.");
    }


}

