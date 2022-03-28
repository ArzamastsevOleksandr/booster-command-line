package cliclient.command.handler;

import api.vocabulary.PatchVocabularyEntryInput;
import api.vocabulary.VocabularyEntryApi;
import api.vocabulary.VocabularyEntryDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.args.CmdArgs;
import cliclient.command.args.UpdateVocabularyEntryCmdArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateVocabularyEntryCommandHandler implements CommandHandler {

    private final VocabularyEntryApi vocabularyEntryApi;
    private final CommandLineAdapter adapter;

    // todo: update contexts
    @Override
    public void handle(CmdArgs cwa) {
        var args = (UpdateVocabularyEntryCmdArgs) cwa;

        var input = new PatchVocabularyEntryInput();

        input.setId(args.getId());
        args.getName().ifPresent(input::setName);
        args.getDefinition().ifPresent(input::setDefinition);
        args.getCorrectAnswersCount().ifPresent(input::setCorrectAnswersCount);

        // todo: support updating synonyms
        VocabularyEntryDto vocabularyEntryDto = vocabularyEntryApi.patchEntry(input);
        adapter.writeLine(vocabularyEntryDto);
    }

    @Override
    public Command getCommand() {
        return Command.UPDATE_VOCABULARY_ENTRY;
    }

}
