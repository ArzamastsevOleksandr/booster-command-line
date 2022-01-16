package notesservice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/notes/")
@RequiredArgsConstructor
class NoteController {

    private final NoteService noteService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    NoteCollection all() {
        return new NoteCollection(noteService.getAll());
    }

}
