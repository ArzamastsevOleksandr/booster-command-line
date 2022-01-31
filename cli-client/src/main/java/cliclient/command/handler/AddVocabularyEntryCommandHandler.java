package cliclient.command.handler;

import api.vocabulary.AddVocabularyEntryInput;
import api.vocabulary.VocabularyEntryControllerApi;
import api.vocabulary.VocabularyEntryDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.AddVocabularyEntryCommandArgs;
import cliclient.command.arguments.CommandArgs;
import cliclient.service.SessionTrackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddVocabularyEntryCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final SessionTrackerService sessionTrackerService;
    private final VocabularyEntryControllerApi vocabularyEntryControllerApi;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (AddVocabularyEntryCommandArgs) commandArgs;
        var input = new AddVocabularyEntryInput();

        input.setName(args.getName());
        args.languageId().ifPresent(input::setLanguageId);
        args.definition().ifPresent(input::setDefinition);
        input.setSynonyms(args.getSynonyms());

        VocabularyEntryDto vocabularyEntryDto = vocabularyEntryControllerApi.add(input);
        // todo: color
        adapter.writeLine(vocabularyEntryDto);
        adapter.writeLine("Entries added so far: " + sessionTrackerService.vocabularyEntriesAddedCount);
    }

    @Override
    public Command getCommand() {
        return Command.ADD_VOCABULARY_ENTRY;
    }

}
