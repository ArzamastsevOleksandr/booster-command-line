package vocabularyservice.vocabularyentry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class TestWordService {

    private final WordRepository wordRepository;

    public Long createWord(String name) {
        var entity = new WordEntity();
        entity.setName(name);
        return wordRepository.save(entity).getId();
    }

}
