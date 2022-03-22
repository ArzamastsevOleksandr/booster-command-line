package api.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettingsDto {
    Long id;

    String defaultLanguageName;

    Integer entriesPerVocabularyTrainingSession;

    Integer vocabularyPagination;
    Integer notesPagination;
    Integer languagesPagination;
    Integer tagsPagination;
}
