    package org.spring.shopagent.config;

    import org.apache.ibatis.annotations.Mapper;
    import org.apache.ibatis.session.SqlSessionFactory;
    import org.mybatis.spring.mapper.MapperFactoryBean;
    import org.reflections.Reflections;
    import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
    import org.springframework.context.ApplicationContext;
    import org.springframework.context.ConfigurableApplicationContext;
    import org.springframework.stereotype.Component;
    import org.spring.shopagent.mapper.ShopMapper;

    import java.beans.Introspector;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Set;

    @Component
    public class DynamicMapperRegistrar {

        public void registerMappers(String basePackage, SqlSessionFactory sqlSessionFactory, ApplicationContext ctx) {
            ConfigurableListableBeanFactory beanFactory =
                    ((ConfigurableApplicationContext) ctx).getBeanFactory();

            // GraalVM Native Image에서 Reflections 라이브러리가 작동하지 않으므로 수동 등록
            System.out.println("Registering mappers manually for Native Image compatibility");

            List<Class<?>> mappers = new ArrayList<>();
            mappers.add(ShopMapper.class);

            System.out.println("Found " + mappers.size() + " mapper(s) to register");

            for (Class<?> mapperClass : mappers) {
                try {
                    System.out.println("Registering mapper: " + mapperClass.getName());

                    MapperFactoryBean<?> factoryBean = new MapperFactoryBean<>(mapperClass);
                    factoryBean.setSqlSessionFactory(sqlSessionFactory);
                    factoryBean.afterPropertiesSet();

                    String beanName = Introspector.decapitalize(mapperClass.getSimpleName());
                    if (!beanFactory.containsBean(beanName)) {
                        beanFactory.registerSingleton(beanName, factoryBean.getObject());
                        System.out.println("✅ Mapper registered successfully: " + beanName);
                    }

                } catch (Exception e) {
                    System.err.println("❌ Mapper registration failed: " + mapperClass.getName());
                    System.err.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }