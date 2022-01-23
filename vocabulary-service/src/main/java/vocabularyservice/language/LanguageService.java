package vocabularyservice.language;

import api.exception.NotFoundException;
import api.vocabulary.AddLanguageInput;
import api.vocabulary.LanguageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;

    @Transactional(readOnly = true)
    Collection<LanguageDto> getAll() {
        return languageRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public LanguageDto toDto(LanguageEntity languageEntity) {
        return new LanguageDto(languageEntity.getId(), languageEntity.getName());
    }

    @Transactional(readOnly = true)
    public LanguageDto findById(Long id) {
        return languageRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Language not found by id: " + id));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public LanguageEntity findEntityById(Long id) {
        return languageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Language not found by id: " + id));
    }

    @Transactional
    public LanguageDto add(AddLanguageInput input) {
        var languageEntity = new LanguageEntity();
        languageEntity.setName(input.name());
        languageEntity = languageRepository.save(languageEntity);
        return toDto(languageEntity);
    }

    void deleteById(Long id) {
        languageRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public LanguageDto findByName(String name) {
        return languageRepository.findByName(name)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Language not found by name: " + name));
    }

}
