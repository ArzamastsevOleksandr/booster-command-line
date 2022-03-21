package cliclient.command.handler;

import api.vocabulary.VocabularyEntryApi;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.DeleteVocabularyEntryCommandArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteVocabularyEntryCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final VocabularyEntryApi vocabularyEntryApi;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (DeleteVocabularyEntryCommandArgs) commandArgs;
        vocabularyEntryApi.deleteById(args.id());
        adapter.writeLine("Done");
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_VOCABULARY_ENTRY;
    }

}
