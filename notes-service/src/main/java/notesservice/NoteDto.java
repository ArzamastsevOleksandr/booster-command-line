package notesservice;

import java.util.UUID;

public record NoteDto(UUID id, String content) {
}
