package api.vocabulary;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class VocabularyEntryDto {

    Long id;
    String name;
    String definition;
    int correctAnswersCount;

    @Builder.Default
    Set<String> synonyms = Set.of();

}
