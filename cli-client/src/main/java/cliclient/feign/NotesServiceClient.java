package cliclient.feign;

import cliclient.dto.NoteCollection;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "notes-service-client", url = "http://localhost:8081/notes")
public interface NotesServiceClient {

    @GetMapping(value = "/")
    NoteCollection getAll();

}
