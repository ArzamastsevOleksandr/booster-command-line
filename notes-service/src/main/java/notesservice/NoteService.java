package notesservice;

import api.exception.NotFoundException;
import api.notes.AddNoteInput;
import api.notes.NoteDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
record NoteService(NoteRepository noteRepository) {

    Collection<NoteDto> findAll() {
        return noteRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(toList());
    }

    private NoteDto toDto(NoteEntity noteEntity) {
        return new NoteDto(noteEntity.getId(), noteEntity.getContent());
    }

    NoteDto findById(Long id) {
        return noteRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Note not found by id: " + id));
    }

    NoteDto add(AddNoteInput input) {
        var noteEntity = new NoteEntity();
        noteEntity.setContent(input.content());
        noteEntity = noteRepository.save(noteEntity);
        return toDto(noteEntity);
    }

    void deleteById(Long id) {
        noteRepository.deleteById(id);
    }

}
