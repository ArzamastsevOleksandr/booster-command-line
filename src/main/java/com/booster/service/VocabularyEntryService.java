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

    public Optional<VocabularyEntry> findById(long id) {
        if (vocabularyEntryDao.countWithId(id) == 1) {
            return Optional.of(vocabularyEntryDao.findById(id));
        }
        return Optional.empty();
    }

    public boolean existsWithId(long id) {
        return vocabularyEntryDao.countWithId(id) == 1;
    }

    public boolean existsWithWordIdAndLanguageId(long wordId, long languageId) {
        return vocabularyEntryDao.countWithWordIdAndLanguageId(wordId, languageId) == 1;
    }

    public boolean existAnyWithLanguageId(Long id) {
        return vocabularyEntryDao.countWithLanguageId(id) > 0;
    }

    public boolean existAnyWithSubstring(String substring) {
        return vocabularyEntryDao.countWithSubstring(substring) > 0;
    }

    public boolean existAny() {
        return vocabularyEntryDao.countAny() > 0;
    }

}
