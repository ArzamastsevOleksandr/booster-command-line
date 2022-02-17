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
    Long defaultLanguageId;
    String defaultLanguageName;
    Integer entriesPerVocabularyTrainingSession;
}
