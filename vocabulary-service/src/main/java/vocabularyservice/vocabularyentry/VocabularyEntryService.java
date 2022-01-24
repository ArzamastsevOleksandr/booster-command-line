package vocabularyservice.vocabularyentry;

import api.exception.NotFoundException;
import api.vocabulary.AddVocabularyEntryInput;
import api.vocabulary.PatchVocabularyEntryInput;
import api.vocabulary.VocabularyEntryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vocabularyservice.language.LanguageEntity;
import vocabularyservice.language.LanguageService;

import java.util.Collection;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class VocabularyEntryService {

    private final VocabularyEntryRepository vocabularyEntryRepository;
    private final WordService wordService;
    private final LanguageService languageService;

    @Transactional(readOnly = true)
    public Collection<VocabularyEntryDto> findAll() {
        return vocabularyEntryRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private VocabularyEntryDto toDto(VocabularyEntryEntity entity) {
        return VocabularyEntryDto.builder()
                .id(entity.getId())
                .name(entity.getWord().getName())
                .correctAnswersCount(entity.getCorrectAnswersCount())
                .definition(entity.getDefinition())
                .lastSeenAt(entity.getLastSeenAt())
                .language(languageService.toDto(entity.getLanguage()))
                .isDifficult(entity.getIsDifficult())
                .synonyms(entity.getSynonyms().stream().map(WordEntity::getName).collect(toSet()))
                .build();
    }

    @Transactional
    public VocabularyEntryDto add(AddVocabularyEntryInput input) {
        var entity = new VocabularyEntryEntity();

        LanguageEntity languageEntity = languageService.findEntityById(input.getLanguageId());
        WordEntity wordEntity = wordService.findByNameOrCreateAndGet(input.getName());

        entity.setWord(wordEntity);
        entity.setLanguage(languageEntity);
        entity.setDefinition(input.getDefinition());
        entity.setCorrectAnswersCount(input.getCorrectAnswersCount());

        Set<WordEntity> synonyms = input.getSynonyms()
                .stream()
                .map(wordService::findByNameOrCreateAndGet)
                .collect(toSet());
        entity.setSynonyms(synonyms);

        return toDto(vocabularyEntryRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public VocabularyEntryDto findById(Long id) {
        return vocabularyEntryRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Vocabulary entry not found by id: " + id));
    }

    public void deleteById(Long id) {
        vocabularyEntryRepository.deleteById(id);
    }

    @Transactional
    public VocabularyEntryDto patch(PatchVocabularyEntryInput input) {
        VocabularyEntryEntity vocabularyEntryEntity = vocabularyEntryRepository.findById(input.getId())
                .orElseThrow(() -> new NotFoundException("Vocabulary entry not found by id: " + input.getId()));

        ofNullable(input.getCorrectAnswersCount()).ifPresent(vocabularyEntryEntity::setCorrectAnswersCount);
        ofNullable(input.getName()).ifPresent(newName -> {
            WordEntity wordEntity = wordService.findByNameOrCreateAndGet(newName);
            vocabularyEntryEntity.setWord(wordEntity);
        });
        ofNullable(input.getDefinition()).ifPresent(vocabularyEntryEntity::setDefinition);
        ofNullable(input.getLastSeenAt()).ifPresent(vocabularyEntryEntity::setLastSeenAt);
        ofNullable(input.getIsDifficult()).ifPresent(vocabularyEntryEntity::setIsDifficult);

        return toDto(vocabularyEntryRepository.save(vocabularyEntryEntity));
    }

}
