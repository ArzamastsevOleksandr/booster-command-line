package vocabularyservice.vocabularyentry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class WordService {

    private final WordRepository wordRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public WordEntity findByNameOrCreateAndGet(String name) {
        return wordRepository.findByName(name).orElseGet(() -> {
            var wordEntity = new WordEntity();
            wordEntity.setName(name);
            return wordRepository.save(wordEntity);
        });
    }

}
