package notesservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
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
            IntStream.rangeClosed(1, 5)
                    .mapToObj(i -> {
                        var noteEntity = new NoteEntity();
                        noteEntity.setId(UUID.randomUUID());
                        noteEntity.setContent("Note " + i);
                        return noteEntity;
                    }).forEach(noteRepository::save);
            log.info("Create sample data: stop");
        };
    }

}
