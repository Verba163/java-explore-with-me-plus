package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import ru.practicum.ewm.client.StatClient;

@SpringBootApplication
@ComponentScan(value = {"ewm", "client"})
public class MainApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MainApplication.class);
        StatClient statClient = context.getBean(StatClient.class);

    }
}
