package cliclient.command.handler;

import api.vocabulary.VocabularyEntryApi;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.args.CmdArgs;
import cliclient.command.args.DeleteVocabularyEntryCmdArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteVocabularyEntryCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final VocabularyEntryApi vocabularyEntryApi;

    @Override
    public void handle(CmdArgs cwa) {
        var args = (DeleteVocabularyEntryCmdArgs) cwa;
        vocabularyEntryApi.deleteById(args.id());
        adapter.writeLine("Done");
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_VOCABULARY_ENTRY;
    }

}
