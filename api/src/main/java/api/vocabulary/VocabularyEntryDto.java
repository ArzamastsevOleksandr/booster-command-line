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
    String language;
    String definition;
    Integer correctAnswersCount;
    Timestamp lastSeenAt;

    @Builder.Default
    Set<String> synonyms = Set.of();
    @Builder.Default
    Set<String> translations = Set.of();

}
