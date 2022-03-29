package cliclient.command.args;

public record StartVocabularyTrainingSessionCmdArgs(VocabularyTrainingSessionMode mode,
                                                    Integer entriesPerVocabularyTrainingSession) implements CmdArgs {
}
