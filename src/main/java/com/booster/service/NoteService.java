package com.booster.service;

import com.booster.dao.NoteDao;
import com.booster.dao.params.AddTagToNoteDaoParams;
import com.booster.model.Note;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteDao noteDao;

    public boolean existsWithId(Long id) {
        return noteDao.countWithId(id) == 1;
    }

    public Note addTag(AddTagToNoteDaoParams params) {
        noteDao.addTag(params);
        return noteDao.findById(params.getNoteId());
    }

}
