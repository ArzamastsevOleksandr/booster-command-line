package booster.command.handler;

import booster.command.Command;
import booster.command.arguments.CommandWithArgs;
import booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarkVocabularyEntryDifficultCommandHandler implements CommandHandler {

    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getId().ifPresent(id -> vocabularyEntryService.markDifficult(id, true));
    }

    @Override
    public Command getCommand() {
        return Command.MARK_VOCABULARY_ENTRY_DIFFICULT;
    }

}
