package org.spring.shopagent;

import org.spring.shopagent.config.MyBatisRuntimeHints;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.event.EventListener;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@ImportRuntimeHints(MyBatisRuntimeHints.class)
@ConfigurationPropertiesScan
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration.class
})
public class ShopAgentApplication {

    private static final String BUILD_VERSION = "2025-10-11T17:21:50+09:00[Asia/Seoul]";

    public static void main(String[] args) {
        SpringApplication.run(ShopAgentApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logVersionInfo() {
        System.out.println("========================================");
        System.out.println("=== ShopAgent Application Started ===");
        System.out.println("=== Build Version: " + BUILD_VERSION + " ===");
        System.out.println("=== Current Time: " + ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME) + " ===");
        System.out.println("========================================");
    }
}
