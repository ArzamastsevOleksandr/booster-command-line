package notesservice;

import lombok.Getter;

@Getter
class NoteByIdNotFoundException extends RuntimeException {

    private final Long id;

    NoteByIdNotFoundException(Long id) {
        super("Note not found by id: " + id);
        this.id = id;
    }

}
