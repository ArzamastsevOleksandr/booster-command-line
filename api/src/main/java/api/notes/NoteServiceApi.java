package api.notes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequestMapping("/notes/")
public interface NoteServiceApi {

    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    Collection<NoteDto> getAll();

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    NoteDto getById(@PathVariable("id") Long id);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    NoteDto add(@RequestBody AddNoteInput input);

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable("id") Long id);

}
