package vocabularyservice.language;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class TestLanguageService {

    private final LanguageRepository languageRepository;

    public Long createLanguage(String name) {
        var languageEntity = new LanguageEntity();
        languageEntity.setName(name);
        return languageRepository.save(languageEntity).getId();
    }

}
