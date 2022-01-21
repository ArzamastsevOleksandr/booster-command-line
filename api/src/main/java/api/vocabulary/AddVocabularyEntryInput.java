package api.vocabulary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddVocabularyEntryInput {

    private String name;
    private String definition;
    private int correctAnswersCount;

    private Long languageId;

    @Builder.Default
    private Set<String> synonyms = Set.of();

}
