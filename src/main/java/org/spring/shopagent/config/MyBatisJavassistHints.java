package org.spring.shopagent.config;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(MyBatisJavassistHints.Registrar.class)
public class MyBatisJavassistHints {
  public static class Registrar implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader cl) {
      hints.reflection().registerType(javassist.util.proxy.ProxyFactory.class,
          MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
          MemberCategory.INVOKE_PUBLIC_METHODS,
          MemberCategory.INVOKE_DECLARED_METHODS,
          MemberCategory.DECLARED_FIELDS);
      hints.reflection().registerType(javassist.util.proxy.MethodHandler.class,
          MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
          MemberCategory.INVOKE_PUBLIC_METHODS);
      hints.reflection().registerType(javassist.util.proxy.ProxyObject.class,
          MemberCategory.INVOKE_PUBLIC_METHODS,
          MemberCategory.DECLARED_FIELDS);
    }
  }
}