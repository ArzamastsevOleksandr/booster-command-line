package com.booster.config;

import com.booster.input.CommandLineReader;
import com.booster.output.CommandLineWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Configuration
public class InputOutputConfig {

    // todo: autocloseable?
    @Bean
    CommandLineReader commandLineReader() {
        return new CommandLineReader(new BufferedReader(new InputStreamReader(System.in)));
    }

    @Bean
    CommandLineWriter commandLineWriter() {
        return new CommandLineWriter();
    }

}
