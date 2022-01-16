package notesservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
class NoteService {

    private final NoteRepository noteRepository;

    Collection<NoteDto> getAll() {
        return noteRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(toList());
    }

    private NoteDto toDto(NoteEntity noteEntity) {
        return new NoteDto(noteEntity.getId(), noteEntity.getContent());
    }

}
