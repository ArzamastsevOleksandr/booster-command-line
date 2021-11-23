package booster.config;

import booster.adapter.CommandLineAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Configuration
class AdapterConfig {

    @Bean
    CommandLineAdapter commandLineAdapter() {
        return new CommandLineAdapter(new BufferedReader(new InputStreamReader(System.in)));
    }

}
