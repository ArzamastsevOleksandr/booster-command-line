package cliclient.command.handler;

import api.vocabulary.AddVocabularyEntryInput;
import api.vocabulary.VocabularyEntryApi;
import api.vocabulary.VocabularyEntryDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.args.AddVocabularyEntryCmdArgs;
import cliclient.command.args.CmdArgs;
import cliclient.service.SessionTrackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddVocabularyEntryCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final SessionTrackerService sessionTrackerService;
    private final VocabularyEntryApi vocabularyEntryApi;

    @Override
    public void handle(CmdArgs cmdArgs) {
        var args = (AddVocabularyEntryCmdArgs) cmdArgs;
        var input = new AddVocabularyEntryInput();

        input.setName(args.getName());
        args.getLanguage().ifPresent(input::setLanguage);
        args.getDefinition().ifPresent(input::setDefinition);
        input.setSynonyms(args.getSynonyms());

        VocabularyEntryDto vocabularyEntryDto = vocabularyEntryApi.create(input);
        adapter.writeLine(vocabularyEntryDto);
        // todo: update tracker
        adapter.writeLine("Entries added so far: " + sessionTrackerService.vocabularyEntriesAddedCount);
    }

    @Override
    public Command getCommand() {
        return Command.ADD_VOCABULARY_ENTRY;
    }

}
