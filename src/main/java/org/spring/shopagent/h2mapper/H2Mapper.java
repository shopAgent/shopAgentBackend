package org.spring.shopagent.h2mapper;

import org.apache.ibatis.annotations.Mapper;
import org.spring.shopagent.info.dto.DatabaseInfoDTO;
import org.spring.shopagent.setting.dto.DbConfigRequestDTO;
import org.spring.shopagent.setting.dto.SearchRequestDTO;

import java.util.List;

@Mapper
public interface H2Mapper {

    String selectNow();
    
    void createTablesIfNotExists();
    
    void addMissingColumnsToSearchConfig();
    
    int countDatabaseInfo();
    
    void insertDefaultDatabaseInfo();

    void updateDatabaseInfo();

    DatabaseInfoDTO selectDatabaseInfo();

    int updateDatabaseInfo(DbConfigRequestDTO dto);

    int deleteDatabaseInfo();

    int insertSearchConfig(SearchRequestDTO dto);

    SearchRequestDTO selectSearchConfigByIdx(Long idx);

    List<SearchRequestDTO> selectSearchConfigByType(String type);

    int updateSearchConfig(SearchRequestDTO dto);

    List<SearchRequestDTO> selectSearchConfigListOrderByUpdateDate();
}
