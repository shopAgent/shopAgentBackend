package org.spring.shopagent.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.h2.jdbcx.JdbcDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.spring.shopagent.h2mapper.H2Mapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class H2DataSourceConfig {

    private final H2DataSourceProperties h2Props;

    @Bean(name = "h2DataSource")
    public DataSource h2DataSource() {
        String url  = h2Props.getUrl();
        String user = (h2Props.getUsername() == null || h2Props.getUsername().isBlank()) ? "sa" : h2Props.getUsername();
        String pwd  = (h2Props.getPassword() == null) ? "" : h2Props.getPassword();

        // Hikari 대신 H2의 XA DataSource 직접 사용
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(url);
        ds.setUser(user);
        ds.setPassword(pwd);
        return ds;
    }

    @Bean(name = "h2SqlSessionFactory")
    public SqlSessionFactory h2SqlSessionFactory(@Qualifier("h2DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);

        // ✅ 매퍼 XML을 클래스패스에서 찾도록
        factory.setMapperLocations(
                new PathMatchingResourcePatternResolver()
                        .getResources("classpath*:h2mapper/**/*.xml")
        );

        // ✅ MyBatis 설정 (지연로딩 끄기: Javassist 이슈 회피)
        org.apache.ibatis.session.Configuration conf = new org.apache.ibatis.session.Configuration();
        conf.setMapUnderscoreToCamelCase(true);
        conf.setLazyLoadingEnabled(false);
        conf.setAggressiveLazyLoading(false);
        factory.setConfiguration(conf);


        return factory.getObject();
    }

    @Bean(name = "h2SqlSessionTemplate")
    public SqlSessionTemplate h2SqlSessionTemplate(@Qualifier("h2SqlSessionFactory") SqlSessionFactory f) {
        return new SqlSessionTemplate(f);
    }

    @Bean(name = "h2Mapper")
    public MapperFactoryBean<H2Mapper> h2Mapper(@Qualifier("h2SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        MapperFactoryBean<H2Mapper> factoryBean = new MapperFactoryBean<>(H2Mapper.class);
        factoryBean.setSqlSessionFactory(sqlSessionFactory);
        return factoryBean;
    }
}