package org.spring.shopagent.info;

import lombok.RequiredArgsConstructor;
import org.spring.shopagent.config.DbAutoInitializer;
import org.spring.shopagent.exception.CustomException;
import org.spring.shopagent.exception.ErrorType;
import org.spring.shopagent.h2mapper.H2Mapper;
import org.spring.shopagent.info.dto.DatabaseInfoResponseDTO;
import org.spring.shopagent.info.dto.DatabaseNameRequestDTO;
import org.spring.shopagent.mapper.ShopMapper;
import org.spring.shopagent.mapper.provider.ShopMapperProvider;
import org.spring.shopagent.response.ApiResponseDto;
import org.spring.shopagent.response.MsgType;
import org.spring.shopagent.response.ResponseUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InfoService {

    private final ShopMapperProvider shopMapperProvider;

    private final H2Mapper h2Mapper;

    public ApiResponseDto<List<String>> getDatabase() {

        DbAutoInitializer.isDbConnectedThrow();

        ShopMapper shopMapper = shopMapperProvider.get();

        List<String> databaseList = shopMapper.selectDatabaseList();

        return ResponseUtils.ok(databaseList, MsgType.DB_CONNECTION_SUCCESS);

    }

    public ApiResponseDto<Void> updateDatabase(DatabaseNameRequestDTO dto) {

        DbAutoInitializer.isDbConnectedThrow();

        ShopMapper shopMapper = shopMapperProvider.get();

        Optional.ofNullable(shopMapper.selectDatabaseCheck(dto.getDatabaseName()))
                .orElseThrow(() -> new CustomException(ErrorType.DB_DATABASE_NOT_FOUND));

        h2Mapper.updateDatabaseName(dto.getDatabaseName());

        return ResponseUtils.ok(MsgType.DB_CONFIG_UPDATE_SUCCESS);

    }

    public ApiResponseDto<DatabaseInfoResponseDTO> getDatabaseSetting() {

        DbAutoInitializer.isDbConnectedThrow();

        DatabaseInfoResponseDTO databaseInfo2 = h2Mapper.selectDatabaseInfo();
        DatabaseInfoResponseDTO databaseInfo = Optional.ofNullable(h2Mapper.selectDatabaseInfo())
                .orElseThrow(() -> new CustomException(ErrorType.DB_CONFIG_NOT_FOUND));


        return ResponseUtils.ok(databaseInfo, MsgType.DB_CONNECTION_SUCCESS);

    }
}
