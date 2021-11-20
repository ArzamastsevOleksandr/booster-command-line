package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarkVocabularyEntryNotDifficultCommandHandler implements CommandHandler {

    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getId().ifPresent(id -> vocabularyEntryService.markDifficult(id, false));
    }

    @Override
    public Command getCommand() {
        return Command.MARK_VOCABULARY_ENTRY_NOT_DIFFICULT;
    }

}
