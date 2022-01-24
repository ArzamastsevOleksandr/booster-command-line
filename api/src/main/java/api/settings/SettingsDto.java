package api.settings;

public record SettingsDto(Long id,
                          Long defaultLanguageId,
                          Integer entriesPerVocabularyTrainingSession) {
}
