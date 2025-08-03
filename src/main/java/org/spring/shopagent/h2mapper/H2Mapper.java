package org.spring.shopagent.h2mapper;

import org.apache.ibatis.annotations.Mapper;
import org.spring.shopagent.info.dto.DatabaseInfoDTO;
import org.spring.shopagent.setting.dto.DbConfigRequestDTO;
import org.spring.shopagent.setting.dto.SearchRequestDTO;

@Mapper
public interface H2Mapper {

    String selectNow();

    void updateDatabaseInfo();

    DatabaseInfoDTO selectDatabaseInfo();

    int updateDatabaseInfo(DbConfigRequestDTO dto);

    int deleteDatabaseInfo();

    int insertSearchConfig(SearchRequestDTO dto);

    SearchRequestDTO selectSearchConfigByIdx(Long idx);

    int updateSearchConfig(SearchRequestDTO dto);
}
