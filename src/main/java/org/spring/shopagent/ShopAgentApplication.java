package org.spring.shopagent;

import org.spring.shopagent.config.MyBatisRuntimeHints;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ImportRuntimeHints;

@ImportRuntimeHints(MyBatisRuntimeHints.class)
@ConfigurationPropertiesScan
@SpringBootApplication(exclude = { 
    DataSourceAutoConfiguration.class,
    org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration.class
})
public class ShopAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopAgentApplication.class, args);
    }
}
