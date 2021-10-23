package com.booster.service;

import com.booster.dao.LanguageDao;
import com.booster.model.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageDao languageDao;

    public Optional<Language> findByName(String name) {
        try {
            return Optional.ofNullable(languageDao.findByName(name));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

}
