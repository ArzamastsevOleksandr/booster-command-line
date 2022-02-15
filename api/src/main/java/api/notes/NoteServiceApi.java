package api.notes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

public interface NoteServiceApi {

    @Deprecated
    @GetMapping(value = "/")
    @ResponseStatus(OK)
    Collection<NoteDto> getAll();

    @GetMapping(value = "/", params = {"limit"})
    @ResponseStatus(OK)
    List<NoteDto> findFirst(@RequestParam(name = "limit") Integer limit);

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

    @GetMapping(value = "/count-all/")
    @ResponseStatus(OK)
    Integer countAll();

}
