package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.DeleteVocabularyEntryCommandArgs;
import cliclient.feign.vocabulary.VocabularyEntryControllerApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteVocabularyEntryCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final VocabularyEntryControllerApiClient vocabularyEntryControllerApiClient;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (DeleteVocabularyEntryCommandArgs) commandArgs;
        vocabularyEntryControllerApiClient.deleteById(args.id());
        adapter.writeLine("Done");
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_VOCABULARY_ENTRY;
    }

}
