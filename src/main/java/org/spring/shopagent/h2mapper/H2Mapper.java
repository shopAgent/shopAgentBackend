package org.spring.shopagent.h2mapper;

import org.apache.ibatis.annotations.Mapper;
import org.spring.shopagent.info.dto.DatabaseInfoResponseDTO;

@Mapper
public interface H2Mapper {

    String selectNow();

    void updateDatabaseName(String databaseName);

    DatabaseInfoResponseDTO selectDatabaseInfo();
}
