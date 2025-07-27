package org.spring.shopagent.setting;

import lombok.RequiredArgsConstructor;
import org.spring.shopagent.common.DynamicDatabaseService;
import org.spring.shopagent.config.DbAutoInitializer;
import org.spring.shopagent.exception.CustomException;
import org.spring.shopagent.exception.ErrorType;
import org.spring.shopagent.h2mapper.H2Mapper;
import org.spring.shopagent.mapper.ShopMapper;
import org.spring.shopagent.mapper.provider.ShopMapperProvider;
import org.spring.shopagent.response.ApiResponseDto;
import org.spring.shopagent.response.MsgType;
import org.spring.shopagent.response.ResponseUtils;
import org.spring.shopagent.setting.dto.DbConfigRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final DynamicDatabaseService dynamicDatabaseService;

    private final ShopMapperProvider shopMapperProvider;

    private final H2Mapper h2Mapper;

    public ApiResponseDto<Void> setupDatabase(@RequestBody DbConfigRequestDTO dto) {
        try {
            Properties props = new Properties();
            props.setProperty("db.url", dto.getUrl());
            props.setProperty("db.username", dto.getUsername());
            props.setProperty("db.password", dto.getPassword());

            try (FileOutputStream out = new FileOutputStream("db-config.yml")) {
                props.store(out, "Saved DB Config");
            } catch (Exception e) {
                throw new CustomException(ErrorType.DB_CONNECTION_FILE_SAVE_ERROR);
            }

            boolean success = dynamicDatabaseService.initializeDatabase(dto.getUrl(), dto.getUsername(), dto.getPassword(), true);

            if (!success)
                throw new CustomException(ErrorType.DB_CONNECTION_ERROR);

        } catch (Exception e) {
            throw new CustomException(ErrorType.DB_CONNECTION_ERROR);
        }
        return ResponseUtils.ok(MsgType.DB_CONNECTION_SUCCESS);
    }

    public ApiResponseDto<Void> getDbConnectionCheck() {

        File file = new File("db-config.yml");
        if (!file.exists()) {
            throw new CustomException(ErrorType.DB_CONFIG_NOT_FOUND);
        }

        ShopMapper shopMapper = shopMapperProvider.get();

        System.out.println(shopMapper.selectNow());
        System.out.println("====================");
        System.out.println(h2Mapper.selectNow());

        if (DbAutoInitializer.dbConnected) {
            return ResponseUtils.ok(MsgType.DB_CONNECTION_SUCCESS);
        } else {
            throw new CustomException(ErrorType.DB_CONNECTION_ERROR);
        }
    }

    public ApiResponseDto<Void> updateDbConfig(@RequestBody DbConfigRequestDTO dto) {
        try {
            // 1. 기존 파일 덮어쓰기
            Properties props = new Properties();
            props.setProperty("db.url", dto.getUrl());
            props.setProperty("db.username", dto.getUsername());
            props.setProperty("db.password", dto.getPassword());

            try (FileOutputStream out = new FileOutputStream("db-config.yml")) {
                props.store(out, "Updated DB Config");
            } catch (IOException e) {
                throw new CustomException(ErrorType.DB_CONNECTION_FILE_SAVE_ERROR);
            }

            // 2. 재연결 시도
            boolean success = dynamicDatabaseService.initializeDatabase(dto.getUrl(), dto.getUsername(), dto.getPassword(), true);
            if (!success) {
                throw new CustomException(ErrorType.DB_CONNECTION_ERROR);
            }

            ShopMapper shopMapper = shopMapperProvider.get();

            System.out.println(shopMapper.selectNow());

            return ResponseUtils.ok(MsgType.DB_CONFIG_UPDATE_SUCCESS);

        } catch (Exception e) {
            throw new CustomException(ErrorType.DB_CONNECTION_ERROR);
        }
    }
}
