package notesservice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class NoteByIdNotFoundException extends RuntimeException {

    private final Long id;

}
