package notesservice;

import java.util.UUID;

record NoteDto(UUID id, String content) {
}
