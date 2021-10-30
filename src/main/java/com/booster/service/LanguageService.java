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

    public Optional<Language> findByName(String name) {
        if (languageDao.countWithName(name) == 1) {
            return Optional.of(languageDao.findByName(name));
        }
        return Optional.empty();
    }

    // todo: fail fast if >1 rows returned
    public boolean existsWithId(long id) {
        return languageDao.countWithId(id) == 1;
    }

}
