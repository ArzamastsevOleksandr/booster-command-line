package com.booster.service;

import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.model.LanguageBeingLearned;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LanguageBeingLearnedService {

    private final LanguageBeingLearnedDao languageBeingLearnedDao;
    private final WrapperService<LanguageBeingLearned> wrapperService;

    public Optional<LanguageBeingLearned> findById(long id) {
        return wrapperService.wrapDataAccessException(() -> languageBeingLearnedDao.findById(id));
    }

    public Optional<LanguageBeingLearned> findByLanguageId(long id) {
        return wrapperService.wrapDataAccessException(() -> languageBeingLearnedDao.findByLanguageId(id));
    }

}
