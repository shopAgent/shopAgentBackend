// src/main/java/org/spring/shopagent/config/MyBatisLoggingHints.java
package org.spring.shopagent.config;

import org.apache.ibatis.logging.stdout.StdOutImpl;
// (옵션) slf4j 쓸 거면 아래도 살려두기
// import org.apache.ibatis.logging.slf4j.Slf4jImpl;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(MyBatisLoggingHints.Registrar.class)
public class MyBatisLoggingHints {
    public static class Registrar implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader cl) {
            Class<?>[] impls = {
                    org.apache.ibatis.logging.slf4j.Slf4jImpl.class,
                    org.apache.ibatis.logging.stdout.StdOutImpl.class,
                    org.apache.ibatis.logging.nologging.NoLoggingImpl.class,
                    org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl.class
            };
            for (Class<?> c : impls) {
                hints.reflection().registerType(c,
                        org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
            }
        }
    }
}

