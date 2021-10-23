package com.booster.service;

import com.booster.dao.VocabularyDao;
import com.booster.model.Vocabulary;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyDao vocabularyDao;

    public Optional<Vocabulary> findById(long id) {
        try {
            Vocabulary vocabulary = vocabularyDao.findById(id);
            return Optional.ofNullable(vocabulary);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Vocabulary> findByNameAndLanguageBeingLearnedId(String name, long id) {
        try {
            Vocabulary vocabulary = vocabularyDao.findByNameAndLanguageBeingLearnedId(name, id);
            return Optional.ofNullable(vocabulary);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

}
