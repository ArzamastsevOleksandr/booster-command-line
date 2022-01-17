package cliclient.feign;

import cliclient.dto.NoteCollection;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "notes-service-client", url = "http://localhost:8081/notes")
public interface NotesServiceClient {

    @GetMapping(value = "/")
    NoteCollection getAll();

    @GetMapping(value = "/{id}")
    NoteResponse findById(@PathVariable("id") Long id);

    @PostMapping(value = "/")
    NoteResponse add(@RequestBody AddNoteInput input);

    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable("id") Long id);

}
