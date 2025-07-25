package org.spring.shopagent.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShopMapper {
    String selectNow();
}