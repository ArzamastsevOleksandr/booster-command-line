package cliclient.command.handler;

import api.vocabulary.PatchVocabularyEntryInput;
import api.vocabulary.VocabularyEntryDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.MarkVocabularyEntryDifficultCommandArgs;
import cliclient.feign.vocabulary.VocabularyEntryControllerApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarkVocabularyEntryDifficultCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final VocabularyEntryControllerApiClient vocabularyEntryControllerApiClient;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (MarkVocabularyEntryDifficultCommandArgs) commandArgs;
        VocabularyEntryDto vocabularyEntryDto = vocabularyEntryControllerApiClient.patchEntry(PatchVocabularyEntryInput.builder()
                .id(args.id())
                .isDifficult(true)
                .build());
        adapter.writeLine(vocabularyEntryDto);
    }

    @Override
    public Command getCommand() {
        return Command.MARK_VOCABULARY_ENTRY_DIFFICULT;
    }

}
