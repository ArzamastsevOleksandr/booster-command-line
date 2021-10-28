package com.booster.service;

import com.booster.dao.VocabularyDao;
import com.booster.model.Vocabulary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyDao vocabularyDao;
    private final WrapperService wrapperService;

    public Optional<Vocabulary> findById(long id) {
        return wrapperService.wrapDataAccessException(() -> vocabularyDao.findById(id));
    }

    public Optional<Vocabulary> findByNameAndLanguageBeingLearnedId(String name, long id) {
        return wrapperService.wrapDataAccessException(() -> vocabularyDao.findByNameAndLanguageBeingLearnedId(name, id));
    }

    public boolean existsWithNameForLanguageBeingLearnedId(String name, long id) {
        int count = vocabularyDao.countWithNameAndLanguageBeingLearnedId(name, id);
        return count > 0;
    }

    public boolean existsWithId(long id) {
        int count = vocabularyDao.countWithId(id);
        return count > 0;
    }

    public boolean existsDefaultWithId(long id) {
        int count = vocabularyDao.countDefaultWithId(id);
        return count > 0;
    }

}
