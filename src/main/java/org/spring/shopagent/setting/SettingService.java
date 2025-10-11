package org.spring.shopagent.setting;

import lombok.RequiredArgsConstructor;
import org.spring.shopagent.common.AiClientCallService;
import org.spring.shopagent.common.DynamicDatabaseService;
import org.spring.shopagent.config.DbAutoInitializer;
import org.spring.shopagent.exception.CustomException;
import org.spring.shopagent.exception.ErrorType;
import org.spring.shopagent.h2mapper.H2Mapper;
import org.spring.shopagent.info.dto.DatabaseInfoDTO;
import org.spring.shopagent.mapper.ShopMapper;
import org.spring.shopagent.mapper.provider.ShopMapperProvider;
import org.spring.shopagent.response.ApiResponseDto;
import org.spring.shopagent.response.MsgType;
import org.spring.shopagent.response.ResponseUtils;
import org.spring.shopagent.setting.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final DynamicDatabaseService dynamicDatabaseService;

    private final ShopMapperProvider shopMapperProvider;

    private final AiClientCallService aiClientCallService;

    private final H2Mapper h2Mapper;


    private static final List<String> MAPPER_NAMES = List.of("shopMapper");

    @Transactional
    public ApiResponseDto<DbConfigRequestDTO> insertDbConfig(@RequestBody DbConfigRequestDTO dto) {
        try {
            System.out.println("=== insertDbConfig START ===");
            System.out.println("Request DTO: " + dto);

            System.out.println("Step 1: Creating JDBC URL...");
            String url = dynamicDatabaseService.createDBJdbcUrl(dto.getDatabaseType(), dto.getDbName(), dto.getUrl(), dto.getDbPort());
            System.out.println("Created JDBC URL: " + url);

            System.out.println("Step 2: Initializing database connection...");
            boolean success = dynamicDatabaseService.initializeDatabase(url, dto.getDbUserName(), dto.getDbPassword(), true);
            System.out.println("Database initialization result: " + success);

            System.out.println("Step 3: Updating H2 database info...");
            h2Mapper.updateDatabaseInfo(dto);
            System.out.println("H2 database info updated successfully");

            if (!success) {
                System.err.println("ERROR: Database initialization failed");
                throw new CustomException(ErrorType.DB_CONNECTION_ERROR);
            }

            System.out.println("=== insertDbConfig SUCCESS ===");
        } catch (CustomException e) {
            System.err.println("=== insertDbConfig FAILED (CustomException) ===");
            System.err.println("Error Type: " + e.getErrorType());
            System.err.println("Error Message: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("=== insertDbConfig FAILED (Exception) ===");
            System.err.println("Exception Type: " + e.getClass().getName());
            System.err.println("Exception Message: " + e.getMessage());
            e.printStackTrace();
            throw new CustomException(ErrorType.DB_CONNECTION_ERROR);
        }
        return ResponseUtils.ok(MsgType.DB_CONNECTION_SUCCESS, dto);
    }

    public ApiResponseDto<DbConnectionTestResponseDTO> dbConnectionCheck(DbConnectionTestRequestDTO dto) {
        String version;

        try {
            String url = dynamicDatabaseService.createDBJdbcUrl(dto.getDatabaseType(), dto.getDbName(), dto.getUrl(), dto.getDbPort());

            boolean success = dynamicDatabaseService.initializeDatabase(url, dto.getDbUserName(), dto.getDbPassword(), true);

            if (!success)
                throw new CustomException(ErrorType.DB_CONNECTION_ERROR);

            version = shopMapperProvider.get().selectVersion();

            dynamicDatabaseService.destroy(MAPPER_NAMES);

        } catch (Exception e) {
            throw new CustomException(ErrorType.DB_CONNECTION_ERROR, e.getMessage());
        }

        DbConnectionTestResponseDTO responseDTO = DbConnectionTestResponseDTO.builder()
                .databaseType(dto.getDatabaseType())
                .dbName(dto.getDbName())
                .url(dto.getUrl())
                .dbPort(dto.getDbPort())
                .dbUserName(dto.getDbUserName())
                .version(version)
                .build();

        return ResponseUtils.ok(MsgType.DB_CONNECTION_SUCCESS, responseDTO);
    }

    @Transactional
    public ApiResponseDto<DbConfigRequestDTO> updateDbConfig(@RequestBody DbConfigRequestDTO dto) {
        try {
            // 1. 기존 파일 덮어쓰기
            String url = dynamicDatabaseService.createDBJdbcUrl(dto.getDatabaseType(), dto.getDbName(), dto.getUrl(), dto.getDbPort());

            // 2. 재연결 시도
            boolean success = dynamicDatabaseService.initializeDatabase(url, dto.getDbUserName(), dto.getDbPassword(), true);
            if (!success) {
                throw new CustomException(ErrorType.DB_CONNECTION_ERROR);
            }

            h2Mapper.updateDatabaseInfo(dto);

            ShopMapper shopMapper = shopMapperProvider.get();

            System.out.println(shopMapper.selectNow());

            return ResponseUtils.ok(MsgType.DB_CONFIG_UPDATE_SUCCESS, dto);

        } catch (Exception e) {
            throw new CustomException(ErrorType.DB_CONNECTION_ERROR);
        }
    }

    public ApiResponseDto<DatabaseInfoDTO> getDbConfig() {

        DatabaseInfoDTO dbConfig = h2Mapper.selectDatabaseInfo();

        return ResponseUtils.ok(MsgType.DATA_SELECT_SUCCESS, dbConfig);

    }

    @Transactional
    public ApiResponseDto<Void> deleteDbConfig() {
        h2Mapper.deleteDatabaseInfo();

        DbAutoInitializer.dbConnected = false;

        try {
            dynamicDatabaseService.destroy(MAPPER_NAMES);
            return ResponseUtils.ok(MsgType.DB_CONNECTION_DISCONNECTED);
        } catch (Exception e) {
            throw new CustomException(ErrorType.DB_CONNECTION_ERROR);
        }
    }

    public ApiResponseDto<AiConnectionTestResponseDTO> aiConnectionCheck() {

        DatabaseInfoDTO dbConfig = h2Mapper.selectDatabaseInfo();

        // AI 별로 분리 필요 현재는 Gemini만 고정

        String model = "gemini-2.5-flash";

        try {

            System.out.println(aiClientCallService.callAiClient("say hello"));

        } catch (Exception e) {

            throw new CustomException(ErrorType.AI_CONNECTION_ERROR, e.getMessage());

        }

        AiConnectionTestResponseDTO response = AiConnectionTestResponseDTO.builder()
                .connection(true)
                .provider(dbConfig.getAiProvider())
                .model(model)
                .build();

        return ResponseUtils.ok(MsgType.AI_CONNECTION_OK, response);
    }

    public ApiResponseDto<Void> insertSearchConfig(SearchRequestDTO dto) {

        DbAutoInitializer.isDbConnectedThrow();

        h2Mapper.insertSearchConfig(dto);

        return ResponseUtils.ok(MsgType.SEARCH_CONFIG_INSERT_SUCCESS);

    }

    public ApiResponseDto<SearchRequestDTO> getSearchConfig(Long idxSearchConfig) {

        DbAutoInitializer.isDbConnectedThrow();

        SearchRequestDTO searchConfig = h2Mapper.selectSearchConfigByIdx(idxSearchConfig);

        return ResponseUtils.ok(MsgType.DATA_SELECT_SUCCESS, searchConfig);

    }

    public ApiResponseDto<Void> updateSearchConfig(SearchRequestDTO dto) {

        DbAutoInitializer.isDbConnectedThrow();

        if (dto.getIdxSearchConfig() == null)
            throw new CustomException(ErrorType.DATA_NOT_FOUND);


        SearchRequestDTO searchConfig = h2Mapper.selectSearchConfigByIdx(dto.getIdxSearchConfig());
        if (searchConfig == null)
            throw new CustomException(ErrorType.DATA_NOT_FOUND);

        h2Mapper.updateSearchConfig(dto);

        return ResponseUtils.ok(MsgType.SEARCH_CONFIG_UPDATE_SUCCESS);

    }

    public ApiResponseDto<List<TableInfoResponseDTO>> getTableList() {

        DbAutoInitializer.isDbConnectedThrow();

        ShopMapper shopMapper = shopMapperProvider.get();
        List<TableInfoResponseDTO> tableInfoList = shopMapper.selectTableInfoList();

        return ResponseUtils.ok(MsgType.DATA_SELECT_SUCCESS, tableInfoList);

    }

    public ApiResponseDto<List<TableColumnResponseDTO>> getTableColumns(String tableName) {

        DbAutoInitializer.isDbConnectedThrow();

        ShopMapper shopMapper = shopMapperProvider.get();
        List<TableColumnResponseDTO> columns = shopMapper.selectTableColumns(tableName);

        return ResponseUtils.ok(MsgType.DATA_SELECT_SUCCESS, columns);

    }

    public ApiResponseDto<List<SearchRequestDTO>> getSearchConfigList() {

        DbAutoInitializer.isDbConnectedThrow();

        List<SearchRequestDTO> searchConfigList = h2Mapper.selectSearchConfigListOrderByUpdateDate();

        return ResponseUtils.ok(MsgType.DATA_SELECT_SUCCESS, searchConfigList);

    }
}
