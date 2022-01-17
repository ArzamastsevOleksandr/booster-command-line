package notesservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class TestNoteService {

    private final NoteRepository noteRepository;

    public Long createNote(String content) {
        var noteEntity = new NoteEntity();
        noteEntity.setContent(content);
        return noteRepository.save(noteEntity).getId();
    }

}
