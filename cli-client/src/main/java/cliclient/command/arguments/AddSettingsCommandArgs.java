package cliclient.command.arguments;

public record AddSettingsCommandArgs(Long languageId,
                                     Integer entriesPerVocabularyTrainingSession) implements CommandArgs {
}
