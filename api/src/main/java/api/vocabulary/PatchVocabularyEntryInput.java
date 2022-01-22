package api.vocabulary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchVocabularyEntryInput {

    private Long id;
    private String name;
    private String definition;
    private Integer correctAnswersCount;

}
