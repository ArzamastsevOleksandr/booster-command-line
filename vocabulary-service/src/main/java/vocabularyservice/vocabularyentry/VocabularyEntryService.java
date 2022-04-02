package vocabularyservice.vocabularyentry;

import api.exception.NotFoundException;
import api.settings.SettingsApi;
import api.settings.SettingsDto;
import api.vocabulary.AddVocabularyEntryInput;
import api.vocabulary.PatchVocabularyEntryInput;
import api.vocabulary.PatchVocabularyEntryLastSeenAtInput;
import api.vocabulary.VocabularyEntryDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vocabularyservice.language.Language;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VocabularyEntryService {

    private final VocabularyEntryRepository vocabularyEntryRepository;
    private final WordService wordService;
    private final SettingsApi settingsApi;

    public List<VocabularyEntryDto> findFirst(Integer limit) {
        return vocabularyEntryRepository.findFirst(limit)
                .map(this::toDto)
                .toList();
    }

    private VocabularyEntryDto toDto(VocabularyEntryEntity entity) {
        return VocabularyEntryDto.builder()
                .id(entity.getId())
                .name(entity.getWord().getName())
                .language(entity.getLanguage().getName())
                .correctAnswersCount(entity.getCorrectAnswersCount())
                .definition(entity.getDefinition())
                .lastSeenAt(entity.getLastSeenAt())
                .synonyms(entity.getSynonyms().stream().map(WordEntity::getName).collect(toSet()))
                .translations(entity.getTranslations().stream().map(WordEntity::getName).collect(toSet()))
                .build();
    }

    // todo: unique constraint on name
    @Transactional
    public VocabularyEntryDto create(AddVocabularyEntryInput input) {
        var entity = new VocabularyEntryEntity();

        Language language = resolveLanguage(input);
        WordEntity wordEntity = wordService.findByNameOrCreateAndGet(input.getName());
        vocabularyEntryRepository.findByWordId(wordEntity.getId()).ifPresent(entry -> {
            throw new IllegalStateException("Vocabulary entry already exists for word: " + wordEntity.getName() + " with id: " + entry.getId());
        });

        entity.setWord(wordEntity);
        entity.setLanguage(language);
        entity.setDefinition(input.getDefinition());
        entity.setCorrectAnswersCount(input.getCorrectAnswersCount());
        // todo: test
        ofNullable(input.getLastSeenAt()).ifPresent(entity::setLastSeenAt);

        Set<WordEntity> synonyms = input.getSynonyms()
                .stream()
                .map(wordService::findByNameOrCreateAndGet)
                .collect(toSet());
        entity.setSynonyms(synonyms);

        Set<WordEntity> translations = input.getTranslations()
                .stream()
                .map(wordService::findByNameOrCreateAndGet)
                .collect(toSet());
        entity.setTranslations(translations);

        return toDto(vocabularyEntryRepository.save(entity));
    }

    private Language resolveLanguage(AddVocabularyEntryInput input) {
        return ofNullable(input.getLanguage())
                .map(String::toUpperCase)
                .map(Language::fromString)
                .orElseGet(() -> {
                    try {
                        SettingsDto settingsDto = settingsApi.findOne();
                        String defaultLanguageName = settingsDto.getDefaultLanguageName();
                        if (defaultLanguageName == null) {
                            throw new NotFoundException("Language not specified and settings do not have a default language");
                        }
                        return Language.fromString(defaultLanguageName);
                    } catch (FeignException.FeignClientException ex) {
                        if (ex.status() == HttpStatus.NOT_FOUND.value()) {
                            throw new NotFoundException("Language not specified and custom settings do not exist");
                        }
                        throw ex;
                    }
                });
    }

    public VocabularyEntryDto findById(Long id) {
        return vocabularyEntryRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Vocabulary entry not found by id: " + id));
    }

    @Transactional
    public void deleteById(Long id) {
        vocabularyEntryRepository.findById(id).ifPresentOrElse(vocabularyEntryRepository::delete, () -> {
            throw new NotFoundException("Vocabulary entry not found by id: " + id);
        });
    }

    @Deprecated
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

        return toDto(vocabularyEntryRepository.save(vocabularyEntryEntity));
    }

    public Collection<VocabularyEntryDto> findWithSynonyms(Integer limit) {
        return vocabularyEntryRepository.findWithSynonyms(limit)
                .map(this::toDto)
                .toList();
    }

    public Integer countAll() {
        return vocabularyEntryRepository.countAllBy();
    }

    public Integer countWithSubstring(String substring) {
        return vocabularyEntryRepository.countAllByWordNameContaining(substring);
    }

    public List<VocabularyEntryDto> findFirstWithSubstring(Integer limit, String substring) {
        return vocabularyEntryRepository.findFirstWithSubstring(limit, "%" + substring + "%")
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public List<VocabularyEntryDto> patchLastSeenAt(PatchVocabularyEntryLastSeenAtInput input) {
        // todo: max size of ids ~ 20
        List<VocabularyEntryEntity> vocabularyEntryEntities = vocabularyEntryRepository.findAllById(input.getIds())
                .stream()
                .peek(vocabularyEntryEntity -> vocabularyEntryEntity.setLastSeenAt(input.getLastSeenAt()))
                .toList();
        return vocabularyEntryRepository.saveAll(vocabularyEntryEntities)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<String> myLanguages() {
        return vocabularyEntryRepository.myLanguages();
    }

    public List<VocabularyEntryDto> findAllByLanguage(String language) {
        return vocabularyEntryRepository.findAllByLanguage(Language.fromString(language))
                .map(this::toDto)
                .toList();
    }

}
