package cliclient.command.arguments;

public record StartVocabularyTrainingSessionCommandArgs(VocabularyTrainingSessionMode mode,
                                                        Integer sessionSize) implements CommandArgs {
}
