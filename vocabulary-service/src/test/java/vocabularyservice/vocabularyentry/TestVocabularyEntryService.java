package vocabularyservice.vocabularyentry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class TestVocabularyEntryService {

    private final VocabularyEntryRepository vocabularyEntryRepository;
    private final WordRepository wordRepository;
    private final TestWordService testWordService;

    public Long createVocabularyEntry(String name) {
        Long id = testWordService.createWord(name);
        WordEntity wordEntity = wordRepository.findById(id).get();

        var entity = new VocabularyEntryEntity();
        entity.setWordEntity(wordEntity);
        entity.setCorrectAnswersCount(0);
        entity.setDefinition(name);
        entity.setSynonyms(Set.of(wordEntity));
        return vocabularyEntryRepository.save(entity).getId();
    }

}
