package notesservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/notes/")
class NotesController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    NotesCollection all() {
        return new NotesCollection(List.of(
                new NoteDto(UUID.randomUUID(), "Note 1"),
                new NoteDto(UUID.randomUUID(), "Note 2"),
                new NoteDto(UUID.randomUUID(), "Note 3")
        ));
    }

}
