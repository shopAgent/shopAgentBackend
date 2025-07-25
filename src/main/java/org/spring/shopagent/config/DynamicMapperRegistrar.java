    package org.spring.shopagent.config;

    import org.apache.ibatis.annotations.Mapper;
    import org.apache.ibatis.session.SqlSessionFactory;
    import org.mybatis.spring.mapper.MapperFactoryBean;
    import org.reflections.Reflections;
    import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
    import org.springframework.context.ApplicationContext;
    import org.springframework.context.ConfigurableApplicationContext;
    import org.springframework.stereotype.Component;

    import java.beans.Introspector;
    import java.util.Set;

    @Component
    public class DynamicMapperRegistrar {

        public void registerMappers(String basePackage, SqlSessionFactory sqlSessionFactory, ApplicationContext ctx) {
            ConfigurableListableBeanFactory beanFactory =
                    ((ConfigurableApplicationContext) ctx).getBeanFactory();

            System.out.println("Mapper 스캔");
            Reflections reflections = new Reflections(basePackage);
            Set<Class<?>> mappers = reflections.getTypesAnnotatedWith(Mapper.class);

            if (mappers.isEmpty()) {
                System.out.println("Mapper 없음: " + basePackage);
            }

            for (Class<?> mapperClass : mappers) {
                try {
                    System.out.println("Mapper 등록: " + mapperClass.getName());

                    MapperFactoryBean<?> factoryBean = new MapperFactoryBean<>(mapperClass);
                    factoryBean.setSqlSessionFactory(sqlSessionFactory);
                    factoryBean.afterPropertiesSet();

                    String beanName = Introspector.decapitalize(mapperClass.getSimpleName());
                    if (!beanFactory.containsBean(beanName)) {
                        beanFactory.registerSingleton(beanName, factoryBean.getObject());
                        System.out.println("Mapper 등록: " + beanName);
                    }

                } catch (Exception e) {
                    System.err.println("Mapper 등록 실패: " + e.getMessage());
                }
            }
        }
    }