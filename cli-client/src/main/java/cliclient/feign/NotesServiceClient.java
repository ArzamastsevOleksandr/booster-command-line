package cliclient.feign;

import cliclient.dto.NoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@FeignClient(value = "notes-service-client", url = "http://localhost:8081/notes")
public interface NotesServiceClient {

    @GetMapping(value = "/")
    Collection<NoteDto> getAll();

    @GetMapping(value = "/{id}")
    NoteDto findById(@PathVariable("id") Long id);

    @PostMapping(value = "/")
    NoteDto add(@RequestBody AddNoteInput input);

    @DeleteMapping(value = "/{id}")
    void delete(@PathVariable("id") Long id);

}
