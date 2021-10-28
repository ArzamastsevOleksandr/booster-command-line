package com.booster.service;

import com.booster.dao.LanguageDao;
import com.booster.model.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageDao languageDao;
    private final WrapperService wrapperService;

    public Optional<Language> findByName(String name) {
        return wrapperService.wrapDataAccessException(() -> languageDao.findByName(name));
    }

    public boolean existsWithId(long id) {
        int count = languageDao.countWithId(id);
        return count > 0;
    }

}
