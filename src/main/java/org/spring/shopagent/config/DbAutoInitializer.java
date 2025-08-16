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
            // 1. H2 DB 테이블 자동 생성
            initializeH2Database();
            
            // 2. 동적 MySQL 연결 시도
            DatabaseInfoDTO dbInfo = h2Mapper.selectDatabaseInfo();

            if (dbInfo == null || dbInfo.getUrl().isEmpty() || dbInfo.getDbUserName().isEmpty() || dbInfo.getDbPassword().isEmpty()) {
                System.out.println("⚠️ MySQL 연결 정보가 설정되지 않았습니다. H2 Console에서 설정하세요.");
                dbConnected = false;
                return;
            }

            String url = dynamicDatabaseService.createDBJdbcUrl(
                    dbInfo.getDatabaseType(),
                    dbInfo.getDbName(),
                    dbInfo.getUrl(),
                    dbInfo.getDbPort());

            dbConnected = dynamicDatabaseService.initializeDatabase(url, dbInfo.getDbUserName(), dbInfo.getDbPassword(), true);
            System.out.println("✅ MySQL 동적 연결 성공!");
            
        } catch (Exception e) {
            System.err.println("❌ 데이터베이스 초기화 실패: " + e.getMessage());
            dbConnected = false;
        }
    }
    
    private void initializeH2Database() {
        try {
            System.out.println("=== H2 DB 초기화 시작 ===");
            
            // 1. 테이블 생성 (없으면)
            h2Mapper.createTablesIfNotExists();
            System.out.println("✅ H2 테이블 생성 완료");
            
            // 2. database_info 테이블에 데이터가 없으면 기본값 삽입
            int dataCount = h2Mapper.countDatabaseInfo();
            if (dataCount == 0) {
                h2Mapper.insertDefaultDatabaseInfo();
                System.out.println("✅ H2 기본 데이터 삽입 완료");
            } else {
                System.out.println("✅ H2 기존 데이터 존재: " + dataCount + "개");
            }
            
        } catch (Exception e) {
            System.err.println("❌ H2 DB 초기화 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void isDbConnectedThrow() {
        if (!dbConnected)
            throw new RuntimeException("DB 연결이 실패했습니다. 설정을 확인하세요.");
    }


}

