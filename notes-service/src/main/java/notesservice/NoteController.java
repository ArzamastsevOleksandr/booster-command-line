package notesservice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/notes/")
@RequiredArgsConstructor
class NoteController {

    private final NoteService noteService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    NoteCollection getAll() {
        return new NoteCollection(noteService.findAll());
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    NoteResponse getById(@PathVariable("id") Long id) {
        return new NoteResponse(noteService.findById(id));
    }

}
