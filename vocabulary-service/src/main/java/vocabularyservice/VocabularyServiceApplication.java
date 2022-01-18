package vocabularyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"vocabularyservice", "api"})
public class VocabularyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VocabularyServiceApplication.class, args);
    }

}
