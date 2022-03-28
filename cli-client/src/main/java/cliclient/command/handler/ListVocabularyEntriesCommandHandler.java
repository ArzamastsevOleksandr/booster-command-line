package cliclient.command.handler;

import api.vocabulary.VocabularyEntryApi;
import api.vocabulary.VocabularyEntryDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.args.ListVocabularyEntriesCmdWithArgs;
import cliclient.command.args.CmdArgs;
import cliclient.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListVocabularyEntriesCommandHandler implements CommandHandler {

    private final VocabularyEntryService vocabularyEntryService;
    private final VocabularyEntryApi vocabularyEntryApi;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CmdArgs cwa) {
        var args = (ListVocabularyEntriesCmdWithArgs) cwa;
        args.id().ifPresentOrElse(this::displayVocabularyEntryById, () -> displayVocabularyEntries(args));
    }

    @Override
    public Command getCommand() {
        return Command.LIST_VOCABULARY_ENTRIES;
    }

    private void displayVocabularyEntryById(Long id) {
        VocabularyEntryDto vocabularyEntryDto = vocabularyEntryApi.findById(id);
        showAndUpdateLastSeenAt(List.of(vocabularyEntryDto));
    }

    private void showAndUpdateLastSeenAt(List<VocabularyEntryDto> vocabularyEntryDtos) {
        vocabularyEntryDtos.forEach(adapter::writeLine);
        vocabularyEntryService.updateLastSeenAt(vocabularyEntryDtos);
    }

    private void displayVocabularyEntries(ListVocabularyEntriesCmdWithArgs args) {
        args.substring().ifPresentOrElse(substring -> {
            var paginator = new Paginator<VocabularyEntryDto>(args.getPagination(), vocabularyEntryApi.countWithSubstring(substring)) {
                List<VocabularyEntryDto> nextBatch() {
                    return vocabularyEntryApi.findFirstWithSubstring(limit(), substring);
                }
            };
            display(paginator);
        }, () -> {
            var paginator = new Paginator<VocabularyEntryDto>(args.getPagination(), vocabularyEntryApi.countAll()) {
                List<VocabularyEntryDto> nextBatch() {
                    return vocabularyEntryApi.findFirst(limit());
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
