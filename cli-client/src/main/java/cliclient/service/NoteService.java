package cliclient.service;

import cliclient.dao.NoteDao;
import cliclient.dao.params.AddNoteDaoParams;
import cliclient.model.Note;
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

    @Deprecated
    public boolean existsWithId(Long id) {
        return noteDao.countWithId(id) == 1;
    }

    @Deprecated
    public Note addTag(String tag, long noteId) {
        noteDao.addTag(tag, noteId);
        return findById(noteId);
    }

    @Deprecated
    public Note findById(long noteId) {
        Note note = noteDao.findById(noteId);
        List<String> tags = noteDao.findTagsByNoteId(noteId);
        return note.toBuilder().tags(new HashSet<>(tags)).build();
    }

    @Deprecated
    public Note add(AddNoteDaoParams params) {
        return transactionTemplate.execute(status -> {
            long noteId = noteDao.add(params.getContent());
            noteDao.addTagsToNote(new ArrayList<>(params.getTags()), noteId);
            sessionTrackerService.incNotesCount(params.getAddCause());
            return findById(noteId);
        });
    }

    @Deprecated
    public List<Note> findAll() {
        List<Note> notes = noteDao.findAll();
        Map<Long, Set<String>> id2Tags = noteDao.queryForNoteId2Tags();

        return notes.stream()
                .map(n -> n.toBuilder().tags(id2Tags.getOrDefault(n.getId(), Set.of())).build())
                .collect(toList());
    }

    @Deprecated
    public void delete(Long id) {
        transactionTemplate.executeWithoutResult(status -> {
            noteDao.removeTagAssociationsById(id);
            noteDao.delete(id);
        });
    }

}
