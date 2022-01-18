package notesservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "/notes/")
record NoteController(NoteService noteService) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Collection<NoteDto> getAll() {
        return noteService.findAll();
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    NoteDto getById(@PathVariable("id") Long id) {
        return noteService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    NoteDto add(@RequestBody AddNoteInput input) {
        return noteService.add(input);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable("id") Long id) {
        noteService.deleteById(id);
    }

}
