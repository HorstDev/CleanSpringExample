package com.example.config;

import com.example.services.MyService;
import com.example.services.MyServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
//@EnableWebMvc
//@ComponentScan("com.example") // место, где будут сканироваться контроллеры
public class AppConfig {

    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }
}
