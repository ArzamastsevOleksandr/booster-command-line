package com.booster.service;

import com.booster.dao.NoteDao;
import com.booster.dao.params.AddNoteDaoParams;
import com.booster.dao.params.AddTagToNoteDaoParams;
import com.booster.model.Note;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteDao noteDao;
    private final TransactionTemplate transactionTemplate;

    public boolean existsWithId(Long id) {
        return noteDao.countWithId(id) == 1;
    }

    public Note addTag(AddTagToNoteDaoParams params) {
        noteDao.addTag(params);
        return noteDao.findById(params.getNoteId());
    }

    // todo: 1 batch insert of tags
    public Note add(AddNoteDaoParams params) {
        return transactionTemplate.execute(status -> {
            long noteId = noteDao.add(params.getContent());
            params.getTags().forEach(tag -> noteDao.addTag(new AddTagToNoteDaoParams(tag, noteId)));
            return noteDao.findById(noteId);
        });
    }

}
