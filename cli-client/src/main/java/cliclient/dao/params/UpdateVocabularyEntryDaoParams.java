package cliclient.dao.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVocabularyEntryDaoParams {

    long id;
    long wordId;
    String definition;
    int correctAnswersCount;

    @Builder.Default
    Set<Long> synonymIds = Set.of();
    @Builder.Default
    Set<Long> antonymIds = Set.of();

}
