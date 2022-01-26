package api.notes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static org.springframework.http.HttpStatus.OK;

public interface NoteServiceApi {

    @GetMapping(value = "/")
    @ResponseStatus(OK)
    Collection<NoteDto> getAll();

    @GetMapping("{id}")
    @ResponseStatus(OK)
    NoteDto getById(@PathVariable("id") Long id);

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    NoteDto add(@RequestBody AddNoteInput input);

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable("id") Long id);

    @PostMapping("/add-tags/")
    @ResponseStatus(OK)
    NoteDto addTags(@RequestBody AddTagsToNoteInput input);

}
