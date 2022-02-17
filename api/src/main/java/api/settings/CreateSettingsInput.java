package api.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSettingsInput {

    Long defaultLanguageId;
    String defaultLanguageName;
    Integer entriesPerVocabularyTrainingSession;

}
