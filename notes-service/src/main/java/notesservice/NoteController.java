package notesservice;

import api.notes.AddNoteInput;
import api.notes.NoteDto;
import api.notes.NoteServiceApi;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
record NoteController(NoteService noteService) implements NoteServiceApi {

    @Override
    public Collection<NoteDto> getAll() {
        return noteService.findAll();
    }

    @Override
    public NoteDto getById(Long id) {
        return noteService.findById(id);
    }

    @Override
    public NoteDto add(@RequestBody AddNoteInput input) {
        return noteService.add(input);
    }

    @Override
    public void deleteById(@PathVariable("id") Long id) {
        noteService.deleteById(id);
    }

}
