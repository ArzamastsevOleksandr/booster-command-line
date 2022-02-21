package vocabularyservice.vocabularyentry;

import api.exception.NotFoundException;
import api.vocabulary.AddVocabularyEntryInput;
import api.vocabulary.PatchVocabularyEntryInput;
import api.vocabulary.PatchVocabularyEntryLastSeenAtInput;
import api.vocabulary.VocabularyEntryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vocabularyservice.language.LanguageEntity;
import vocabularyservice.language.LanguageService;

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
    private final LanguageService languageService;

    public List<VocabularyEntryDto> findFirst(Integer limit) {
        return vocabularyEntryRepository.findFirst(limit)
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
        // todo: test
        ofNullable(input.getLastSeenAt()).ifPresent(entity::setLastSeenAt);

        Set<WordEntity> synonyms = input.getSynonyms()
                .stream()
                .map(wordService::findByNameOrCreateAndGet)
                .collect(toSet());
        entity.setSynonyms(synonyms);

        return toDto(vocabularyEntryRepository.save(entity));
    }

    public VocabularyEntryDto findById(Long id) {
        return vocabularyEntryRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Vocabulary entry not found by id: " + id));
    }

    @Transactional
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

        return toDto(vocabularyEntryRepository.save(vocabularyEntryEntity));
    }

    public Collection<VocabularyEntryDto> findAllByLanguageId(Long id) {
        return vocabularyEntryRepository.findAllByLanguageId(id)
                .map(this::toDto)
                .toList();
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

}
