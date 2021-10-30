package com.booster.service;

import com.booster.dao.WordDao;
import com.booster.model.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordDao wordDao;
    private final WrapperService wrapperService;

    public Word findByNameOrCreateAndGet(String name) {
        return findByName(name)
                .orElseGet(() -> wordDao.createWordWithName(name));
    }

//    todo: if exists - findByName, else - empty optional
    public Optional<Word> findByName(String name) {
        return wrapperService.wrapDataAccessException(() -> wordDao.findByName(name));
    }

}
