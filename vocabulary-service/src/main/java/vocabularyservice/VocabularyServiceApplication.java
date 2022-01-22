package vocabularyservice;

import api.vocabulary.AddLanguageInput;
import api.vocabulary.AddVocabularyEntryInput;
import api.vocabulary.LanguageDto;
import api.vocabulary.VocabularyEntryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import vocabularyservice.language.LanguageService;
import vocabularyservice.vocabularyentry.VocabularyEntryService;

import java.util.Set;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
@ComponentScan(basePackages = {"vocabularyservice", "api"})
public class VocabularyServiceApplication {

    private final LanguageService languageService;
    private final VocabularyEntryService vocabularyEntryService;

    public static void main(String[] args) {
        SpringApplication.run(VocabularyServiceApplication.class, args);
    }

    @Bean
    @Profile("default")
    CommandLineRunner commandLineRunner() {
        return args -> {
            log.info("Create sample data: start");

            LanguageDto languageDto = languageService.add(new AddLanguageInput("English"));
            log.info("Sample language created: " + languageDto);

            VocabularyEntryDto vocabularyEntryDto = vocabularyEntryService.add(AddVocabularyEntryInput.builder()
                    .name("wade")
                    .synonyms(Set.of("ford", "paddle"))
                    .languageId(languageDto.id())
                    .definition("walk with effort")
                    .build());
            log.info("Sample vocabulary entry created: " + vocabularyEntryDto);

            log.info("Create sample data: stop");
        };
    }

}
