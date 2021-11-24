package booster.service;

import booster.dao.NoteDao;
import booster.dao.params.AddNoteDaoParams;
import booster.model.Note;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteDao noteDao;
    private final TransactionTemplate transactionTemplate;
    private final SessionTrackerService sessionTrackerService;

    public boolean existsWithId(Long id) {
        return noteDao.countWithId(id) == 1;
    }

    public Note addTag(String tag, long noteId) {
        noteDao.addTag(tag, noteId);
        return findById(noteId);
    }

    public Note findById(long noteId) {
        Note note = noteDao.findById(noteId);
        List<String> tags = noteDao.findTagsByNoteId(noteId);
        return note.toBuilder().tags(new HashSet<>(tags)).build();
    }

    public Note add(AddNoteDaoParams params) {
        return transactionTemplate.execute(status -> {
            long noteId = noteDao.add(params.getContent());
            noteDao.addTagsToNote(new ArrayList<>(params.getTags()), noteId);
            sessionTrackerService.incNotesCount(params.getAddCause());
            return findById(noteId);
        });
    }

    public List<Note> findAll() {
        List<Note> notes = noteDao.findAll();
        Map<Long, Set<String>> id2Tags = noteDao.queryForNoteId2Tags();

        return notes.stream()
                .map(n -> n.toBuilder().tags(id2Tags.getOrDefault(n.getId(), Set.of())).build())
                .collect(toList());
    }

    public void delete(Long id) {
        transactionTemplate.executeWithoutResult(status -> {
            noteDao.removeTagAssociationsById(id);
            noteDao.delete(id);
        });
    }

}
