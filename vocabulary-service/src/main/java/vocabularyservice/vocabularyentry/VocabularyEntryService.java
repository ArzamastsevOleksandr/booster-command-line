package vocabularyservice.vocabularyentry;

import api.vocabulary.VocabularyEntryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class VocabularyEntryService {

    private final VocabularyEntryRepository vocabularyEntryRepository;

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
                .name(entity.getWordEntity().getName())
                .correctAnswersCount(entity.getCorrectAnswersCount())
                .definition(entity.getDefinition())
                .synonyms(entity.getSynonyms().stream().map(WordEntity::getName).collect(toSet()))
                .build();
    }

}
