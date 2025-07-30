package org.spring.shopagent.setting;

import lombok.RequiredArgsConstructor;
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
import org.spring.shopagent.setting.dto.DbConfigRequestDTO;
import org.spring.shopagent.setting.dto.DbConnectionTestRequestDTO;
import org.spring.shopagent.setting.dto.DbConnectionTestResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final DynamicDatabaseService dynamicDatabaseService;

    private final ShopMapperProvider shopMapperProvider;

    private final H2Mapper h2Mapper;

    private static final List<String> MAPPER_NAMES = List.of("shopMapper");

    @Transactional
    public ApiResponseDto<DbConfigRequestDTO> insertDbConfig(@RequestBody DbConfigRequestDTO dto) {
        try {
            String url = dynamicDatabaseService.createDBJdbcUrl(dto.getDatabaseType(), dto.getDbName(), dto.getUrl(), dto.getDbPort());

            boolean success = dynamicDatabaseService.initializeDatabase(url, dto.getDbUserName(), dto.getDbPassword(), true);

            h2Mapper.updateDatabaseInfo(dto);

            if (!success)
                throw new CustomException(ErrorType.DB_CONNECTION_ERROR);

        } catch (Exception e) {
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
}
