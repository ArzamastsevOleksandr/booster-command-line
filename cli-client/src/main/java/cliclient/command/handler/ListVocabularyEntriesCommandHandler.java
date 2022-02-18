package cliclient.command.handler;

import api.vocabulary.VocabularyEntryDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.ListVocabularyEntriesCommandArgs;
import cliclient.feign.vocabulary.VocabularyEntryControllerApiClient;
import cliclient.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListVocabularyEntriesCommandHandler implements CommandHandler {

    private final VocabularyEntryService vocabularyEntryService;
    private final VocabularyEntryControllerApiClient vocabularyEntryClient;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (ListVocabularyEntriesCommandArgs) commandArgs;
        args.id().ifPresentOrElse(this::displayVocabularyEntryById, () -> displayVocabularyEntries(args));
    }

    @Override
    public Command getCommand() {
        return Command.LIST_VOCABULARY_ENTRIES;
    }

    private void displayVocabularyEntryById(Long id) {
        VocabularyEntryDto vocabularyEntryDto = vocabularyEntryClient.findById(id);
        showAndUpdateLastSeenAt(List.of(vocabularyEntryDto));
    }

    private void showAndUpdateLastSeenAt(List<VocabularyEntryDto> vocabularyEntryDtos) {
        vocabularyEntryDtos.forEach(adapter::writeLine);
        vocabularyEntryService.updateLastSeenAt(vocabularyEntryDtos);
    }

    private void displayVocabularyEntries(ListVocabularyEntriesCommandArgs args) {
        args.substring().ifPresentOrElse(substring -> {
            var paginator = new Paginator<VocabularyEntryDto>(args.pagination(), vocabularyEntryClient.countWithSubstring(substring)) {
                List<VocabularyEntryDto> nextBatch() {
                    return vocabularyEntryClient.findFirstWithSubstring(limit(), substring);
                }
            };
            display(paginator);
        }, () -> {
            var paginator = new Paginator<VocabularyEntryDto>(args.pagination(), vocabularyEntryClient.countAll()) {
                List<VocabularyEntryDto> nextBatch() {
                    return vocabularyEntryClient.findFirst(limit());
                }
            };
            display(paginator);
        });
    }

    private void display(Paginator<VocabularyEntryDto> paginator) {
        if (paginator.isEmpty()) {
            adapter.error("No records");
        } else {
            displayAndUpdateLastSeenAt(paginator);

            String line = readLineIfInRangeOrEnd(paginator);
            while (!line.equals("e") && paginator.isInRange()) {
                displayAndUpdateLastSeenAt(paginator);
                line = readLineIfInRangeOrEnd(paginator);
            }
        }
    }

    private String readLineIfInRangeOrEnd(Paginator<VocabularyEntryDto> paginator) {
        return paginator.isInRange() ? adapter.readLine() : "e";
    }

    private void displayAndUpdateLastSeenAt(Paginator<VocabularyEntryDto> paginator) {
        adapter.writeLine(paginator.counter());
        showAndUpdateLastSeenAt(paginator.nextBatchAndUpdateRange());
    }

}
