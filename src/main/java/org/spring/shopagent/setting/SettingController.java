package org.spring.shopagent.setting;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.spring.shopagent.config.DynamicDataSourceConfig;
import org.spring.shopagent.config.DynamicMapperRegistrar;
import org.spring.shopagent.mapper.ShopMapper;
import org.spring.shopagent.setting.dto.DbConfigRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.FileOutputStream;
import java.util.Properties;

@RestController
@RequiredArgsConstructor
public class SettingController {

    private final ApplicationContext ctx;
    private final DynamicDataSourceConfig dynamicDataSourceConfig;
    private final DynamicMapperRegistrar dynamicMapperRegistrar;

    @PostMapping("/config/db")
    public ResponseEntity<String> setup(@RequestBody DbConfigRequest request) {
        try {
            // 파일 저장
            Properties props = new Properties();
            props.setProperty("db.url", request.getUrl());
            props.setProperty("db.username", request.getUsername());
            props.setProperty("db.password", request.getPassword());
            try (FileOutputStream out = new FileOutputStream("db-config.properties")) {
                props.store(out, "Saved DB Config");
            }

            // 재연결 시도
            DataSource dataSource = dynamicDataSourceConfig.initialize(request.getUrl(), request.getUsername(), request.getPassword(), ctx);
            SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
            factoryBean.setDataSource(dataSource);
            factoryBean.setMapperLocations(
                    new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/**/*.xml"));
            SqlSessionFactory sqlSessionFactory = factoryBean.getObject();
            dynamicMapperRegistrar.registerMappers("org.spring.shopagent.mapper", sqlSessionFactory, ctx);
            ShopMapper shopMapper = ctx.getBean(ShopMapper.class);
            System.out.println(" select Result: " + shopMapper.selectNow());
            return ResponseEntity.ok("연결 성공");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("연결 실패: " + e.getMessage());
        }
    }

    @GetMapping("/db")
    public ResponseEntity<String> getDbConfig() {

        return ResponseEntity.ok("DB Config retrieval not implemented yet.");
    }
}
