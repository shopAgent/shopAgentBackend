package org.spring.shopagent.config;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.ImportRuntimeHints;

public class MyBatisRuntimeHints implements RuntimeHintsRegistrar {
  @Override
  public void registerHints(RuntimeHints hints, ClassLoader cl) {
    // 매퍼 XML들
    hints.resources().registerPattern("h2mapper/**");
    // MyBatis 내장 DTD들
    hints.resources().registerPattern("org/apache/ibatis/builder/xml/mybatis-3-mapper.dtd");
    hints.resources().registerPattern("org/apache/ibatis/builder/xml/mybatis-3-config.dtd");
  }
}