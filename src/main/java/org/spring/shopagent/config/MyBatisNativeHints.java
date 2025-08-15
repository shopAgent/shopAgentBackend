package org.spring.shopagent.config;

import java.util.List;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(MyBatisNativeHints.Registrar.class)
public class MyBatisNativeHints {
    public static class Registrar implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader cl) {

            // MyBatis (repackaged) Javassist
            List<String> keep = List.of(
                    "org.apache.ibatis.executor.loader.javassist.JavassistProxyFactory",
                    "org.apache.ibatis.javassist.util.proxy.ProxyFactory",
                    "org.apache.ibatis.javassist.util.proxy.MethodHandler",
                    "org.apache.ibatis.javassist.util.proxy.ProxyObject"
            );
            for (String name : keep) {
                hints.reflection().registerType(
                        TypeReference.of(name),
                        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_PUBLIC_METHODS,
                        MemberCategory.INVOKE_DECLARED_METHODS,
                        MemberCategory.DECLARED_FIELDS
                );
            }

            // XML/Raw 스크립팅 드라이버
            List<String> scripting = List.of(
                    "org.apache.ibatis.scripting.xmltags.XMLLanguageDriver",
                    "org.apache.ibatis.scripting.defaults.RawLanguageDriver"
            );
            for (String name : scripting) {
                hints.reflection().registerType(
                        TypeReference.of(name),
                        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_PUBLIC_METHODS,
                        MemberCategory.INVOKE_DECLARED_METHODS
                );
            }

            // 매퍼 XML 및 설정 파일
            hints.resources().registerPattern("h2mapper/**");
            hints.resources().registerPattern("mybatis-config.xml");

            // ✅ MyBatis 내장 DTD(네이티브 이미지에서 꼭 포함)
            hints.resources().registerPattern("org/apache/ibatis/builder/xml/mybatis-3-mapper.dtd");
            hints.resources().registerPattern("org/apache/ibatis/builder/xml/mybatis-3-config.dtd");
        }
    }
}
