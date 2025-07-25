    package org.spring.shopagent;

    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

    @SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
    public class ShopAgentApplication {

        public static void main(String[] args) {
            SpringApplication.run(ShopAgentApplication.class, args);
        }

    }
