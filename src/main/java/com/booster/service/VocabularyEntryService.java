package com.booster.service;

import com.booster.dao.VocabularyEntryDao;
import com.booster.model.VocabularyEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VocabularyEntryService {

    private final VocabularyEntryDao vocabularyEntryDao;
    private final WrapperService wrapperService;

    public Optional<VocabularyEntry> findById(long id) {
        return wrapperService.wrapDataAccessException(() -> vocabularyEntryDao.findById(id));
    }

    public boolean existsWithId(long id) {
        int count = vocabularyEntryDao.countWithId(id);
        return count > 0;
    }

    public boolean existsWithWordIdAndLanguageId(long wordId, long languageId) {
        int count = vocabularyEntryDao.countWithWordIdAndLanguageId(wordId, languageId);
        return count > 0;
    }

}
