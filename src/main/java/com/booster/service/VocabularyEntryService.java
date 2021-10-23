package com.booster.service;

import com.booster.dao.VocabularyEntryDao;
import com.booster.model.VocabularyEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VocabularyEntryService {

    private final VocabularyEntryDao vocabularyEntryDao;

    public Optional<VocabularyEntry> findById(long id) {
        try {
            VocabularyEntry vocabularyEntry = vocabularyEntryDao.findById(id);
            return Optional.ofNullable(vocabularyEntry);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

}
