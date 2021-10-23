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
    private final WrapperService<Vocabulary> wrapperService;

    public Optional<Vocabulary> findById(long id) {
        return wrapperService.wrapDataAccessException(() -> vocabularyDao.findById(id));
    }

    public Optional<Vocabulary> findByNameAndLanguageBeingLearnedId(String name, long id) {
        return wrapperService.wrapDataAccessException(() -> vocabularyDao.findByNameAndLanguageBeingLearnedId(name, id));
    }

}
