package org.spring.shopagent.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShopMapper {
    String selectNow();

    String selectVersion();

    List<String> selectDatabaseList();

    String selectDatabaseCheck(String databaseName);

}