package com.booster.service;

import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.dao.VocabularyDao;
import com.booster.dao.params.AddVocabularyDaoParams;
import com.booster.model.LanguageBeingLearned;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LanguageBeingLearnedService {

    private final LanguageBeingLearnedDao languageBeingLearnedDao;
    private final VocabularyDao vocabularyDao;
    private final WrapperService wrapperService;

    public Optional<LanguageBeingLearned> findById(long id) {
        return wrapperService.wrapDataAccessException(() -> languageBeingLearnedDao.findById(id));
    }

    public Optional<LanguageBeingLearned> findByLanguageId(long id) {
        return wrapperService.wrapDataAccessException(() -> languageBeingLearnedDao.findByLanguageId(id));
    }

    // todo: ensure operations are executed in 1 transaction
    public long addWithDefaultVocabulary(long languageId) {
        long languageBeingLearnedId = languageBeingLearnedDao.add(languageId);
        vocabularyDao.add(AddVocabularyDaoParams.builder()
                .languageBeingLearnedId(languageBeingLearnedId)
                .build());
        return languageBeingLearnedId;
    }

    public boolean existsWithId(long id) {
        int count = languageBeingLearnedDao.countWithId(id);
        return count > 0;
    }

    public boolean existsWithLanguageId(long id) {
        int count = languageBeingLearnedDao.countWithLanguageId(id);
        return count > 0;
    }

}
