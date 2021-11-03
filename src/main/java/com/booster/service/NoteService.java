package com.booster.service;

import com.booster.dao.NoteDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteDao noteDao;

    public boolean existsWithId(Long id) {
        return noteDao.countWithId(id) == 1;
    }

}
