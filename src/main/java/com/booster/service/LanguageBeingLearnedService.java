package com.booster.service;

import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.model.LanguageBeingLearned;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LanguageBeingLearnedService {

    private final LanguageBeingLearnedDao languageBeingLearnedDao;

    public Optional<LanguageBeingLearned> findById(long id) {
        // todo: generic component-wrapper of DataAccessException producers
        try {
            var languageBeingLearned = languageBeingLearnedDao.findById(id);

            return Optional.ofNullable(languageBeingLearned);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<LanguageBeingLearned> findByLanguageId(long id) {
        try {
            var languageBeingLearned = languageBeingLearnedDao.findByLanguageId(id);
            return Optional.ofNullable(languageBeingLearned);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

}
