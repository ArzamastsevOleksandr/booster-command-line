package com.booster.config;

import com.booster.adapter.CommandLineAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Configuration
public class AdapterConfig {

    @Bean
    CommandLineAdapter commandLineAdapter() {
        return new CommandLineAdapter(new BufferedReader(new InputStreamReader(System.in)));
    }

}
