package cliclient.config;

import cliclient.adapter.CommandLineAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Configuration
class Cfg {

    @Bean
    CommandLineAdapter commandLineAdapter() {
        return new CommandLineAdapter(new BufferedReader(new InputStreamReader(System.in)));
    }

    @Bean
    ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

}
