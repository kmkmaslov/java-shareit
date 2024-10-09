package ru.practicum.shareit.cfg;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.practicum.shareit.utils.ObjectMapperFactory;


@Configuration
public class ApplicationConfig {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return ObjectMapperFactory.getMapper();
    }

}
