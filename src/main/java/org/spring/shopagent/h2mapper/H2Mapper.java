package org.spring.shopagent.h2mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface H2Mapper {

    String selectNow();

}
