package cliclient.command.handler;

import api.vocabulary.PatchVocabularyEntryInput;
import api.vocabulary.VocabularyEntryDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.UpdateVocabularyEntryCommandArgs;
import cliclient.feign.vocabulary.VocabularyEntryControllerApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateVocabularyEntryCommandHandler implements CommandHandler {

    private final VocabularyEntryControllerApiClient vocabularyEntryControllerApiClient;
    private final CommandLineAdapter adapter;

    // todo: update contexts
    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (UpdateVocabularyEntryCommandArgs) commandArgs;

        var input = new PatchVocabularyEntryInput();

        input.setId(args.getId());
        args.getName().ifPresent(input::setName);
        args.getDefinition().ifPresent(input::setDefinition);
        args.getCorrectAnswersCount().ifPresent(input::setCorrectAnswersCount);

        // todo: support updating synonyms
        VocabularyEntryDto vocabularyEntryDto = vocabularyEntryControllerApiClient.patchEntry(input);
        adapter.writeLine(vocabularyEntryDto);
    }

    @Override
    public Command getCommand() {
        return Command.UPDATE_VOCABULARY_ENTRY;
    }

}
