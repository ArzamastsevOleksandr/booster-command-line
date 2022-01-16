package notesservice;

import java.util.Collection;

public record NotesCollection(Collection<NoteDto> notes) {
}
