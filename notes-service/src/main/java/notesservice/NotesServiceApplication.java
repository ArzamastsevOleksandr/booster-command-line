package notesservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"notesservice", "api"})
@EnableFeignClients(basePackages = {"api"})
class NotesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotesServiceApplication.class, args);
    }

}
