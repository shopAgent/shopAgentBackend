package org.spring.shopagent.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.spring.shopagent.setting.dto.TableColumnResponseDTO;
import org.spring.shopagent.setting.dto.TableInfoResponseDTO;

import java.util.List;
import java.util.Map;

@Mapper
public interface ShopMapper {
    String selectNow();

    String selectVersion();

    List<String> selectDatabaseList();

    String selectDatabaseCheck(String databaseName);

    @Select("${query}")
    List<Map<String, Object>> executeDynamicQuery(String query);

    List<String> selectTableList();

    List<TableColumnResponseDTO> selectTableColumns(String tableName);

    List<TableInfoResponseDTO> selectTableInfoList();

}