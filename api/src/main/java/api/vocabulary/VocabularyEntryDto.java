package api.vocabulary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VocabularyEntryDto {

    Long id;
    String name;
    String definition;
    Integer correctAnswersCount;
    Timestamp lastSeenAt;

    LanguageDto language;

    @Builder.Default
    Set<String> synonyms = Set.of();

}
