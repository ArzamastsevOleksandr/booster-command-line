package com.booster.service;

import com.booster.command.arguments.TrainingSessionMode;
import com.booster.dao.VocabularyEntryDao;
import com.booster.dao.params.AddTagToVocabularyEntryDaoParams;
import com.booster.dao.params.AddVocabularyEntryDaoParams;
import com.booster.model.VocabularyEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VocabularyEntryService {

    private final VocabularyEntryDao vocabularyEntryDao;
    private final TransactionTemplate transactionTemplate;
    private final SessionTrackerService sessionTrackerService;

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

    public boolean existAnyForTrainingMode(TrainingSessionMode mode) {
        switch (mode) {
            case SYNONYMS:
                return vocabularyEntryDao.countWithSynonyms() > 0;
            case ANTONYMS:
                return vocabularyEntryDao.countWithAntonyms() > 0;
            case FULL:
                return vocabularyEntryDao.countWithAntonymsAndSynonyms() > 0;
            default:
                return false;
        }
    }

    public void delete(long id) {
        transactionTemplate.executeWithoutResult(result -> {
            vocabularyEntryDao.deleteSynonyms(id);
            vocabularyEntryDao.deleteAntonyms(id);
            vocabularyEntryDao.deleteContexts(id);
            vocabularyEntryDao.delete(id);
        });
    }

    public VocabularyEntry addTag(AddTagToVocabularyEntryDaoParams params) {
        vocabularyEntryDao.addTag(params);
        return vocabularyEntryDao.findById(params.getVocabularyEntryId());
    }

    public VocabularyEntry add(AddVocabularyEntryDaoParams params) {
        long id = vocabularyEntryDao.addWithDefaultValues(params);
        sessionTrackerService.incVocabularyEntriesAddedCount();
        return vocabularyEntryDao.findById(id);
    }

}
