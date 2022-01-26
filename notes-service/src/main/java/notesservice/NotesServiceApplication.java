package notesservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

import java.util.stream.LongStream;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"notesservice", "api"})
@RequiredArgsConstructor
@EnableFeignClients
class NotesServiceApplication {

    private final NoteRepository noteRepository;

    public static void main(String[] args) {
        SpringApplication.run(NotesServiceApplication.class, args);
    }

    @Bean
    @Profile("default")
    CommandLineRunner commandLineRunner() {
        return args -> {
            log.info("Create sample data: start");
            LongStream.rangeClosed(1, 5)
                    .mapToObj(i -> {
                        var noteEntity = new NoteEntity();
                        noteEntity.setId(i);
                        noteEntity.setContent("Note " + i);
                        return noteEntity;
                    }).forEach(noteRepository::save);
            log.info("Create sample data: stop");
        };
    }

}
