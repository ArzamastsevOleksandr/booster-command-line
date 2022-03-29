package cliclient.command.args;

import cliclient.command.Command;

public record StartVocabularyTrainingSessionCmdArgs(VocabularyTrainingSessionMode mode,
                                                    Integer entriesPerVocabularyTrainingSession) implements CmdArgs {

    @Override
    public Command getCommand() {
        return Command.START_VOCABULARY_TRAINING_SESSION;
    }

}
