package vocabularyservice.vocabularyentry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vocabularyservice.language.LanguageEntity;
import vocabularyservice.language.LanguageRepository;

import java.util.Set;

@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class TestVocabularyEntryService {

    private final VocabularyEntryRepository vocabularyEntryRepository;
    private final WordRepository wordRepository;
    private final LanguageRepository languageRepository;

    // todo: test
    public Long createVocabularyEntry(Long wordId, Long languageId) {
        WordEntity wordEntity = wordRepository.findById(wordId).get();
        LanguageEntity languageEntity = languageRepository.findById(languageId).get();

        var entity = new VocabularyEntryEntity();
        entity.setWord(wordEntity);
        entity.setCorrectAnswersCount(0);
        entity.setDefinition(wordEntity.getName());
        entity.setLanguage(languageEntity);
        entity.setSynonyms(Set.of(wordEntity));
        return vocabularyEntryRepository.save(entity).getId();
    }

}
