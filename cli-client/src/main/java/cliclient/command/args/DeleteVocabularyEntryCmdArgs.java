package cliclient.command.args;

import cliclient.command.Command;

public record DeleteVocabularyEntryCmdArgs(Long id) implements CmdArgs {

    @Override
    public Command getCommand() {
        return Command.DELETE_VOCABULARY_ENTRY;
    }

}
