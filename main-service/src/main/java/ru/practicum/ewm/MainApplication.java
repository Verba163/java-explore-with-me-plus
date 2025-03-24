package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.ewm.client.StatClientImpl;

@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) {
       ConfigurableApplicationContext context = SpringApplication.run(MainApplication.class, args);
        StatClientImpl statClient = context.getBean(StatClientImpl.class);
        System.out.println(statClient.contextTest());
    }
}
