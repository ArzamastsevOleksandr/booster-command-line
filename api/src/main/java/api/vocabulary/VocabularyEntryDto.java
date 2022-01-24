package api.vocabulary;

import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;
import java.util.Set;

@Value
@Builder
public class VocabularyEntryDto {

    Long id;
    String name;
    String definition;
    Integer correctAnswersCount;
    Boolean isDifficult;
    Timestamp lastSeenAt;

    LanguageDto language;

    @Builder.Default
    Set<String> synonyms = Set.of();

}
