package booster.command.handler;

import booster.command.Command;
import booster.command.arguments.CommandArgs;
import booster.command.arguments.MarkVocabularyEntryDifficultCommandArgs;
import booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarkVocabularyEntryDifficultCommandHandler implements CommandHandler {

    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (MarkVocabularyEntryDifficultCommandArgs) commandArgs;
        vocabularyEntryService.markDifficult(args.id(), true);
    }

    @Override
    public Command getCommand() {
        return Command.MARK_VOCABULARY_ENTRY_DIFFICULT;
    }

}
