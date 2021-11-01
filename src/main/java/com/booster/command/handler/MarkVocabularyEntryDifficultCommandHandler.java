package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.VocabularyEntryDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarkVocabularyEntryDifficultCommandHandler implements CommandHandler {

    private final VocabularyEntryDao vocabularyEntryDao;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getId().ifPresent(id -> vocabularyEntryDao.markDifficult(id, true));
    }

    @Override
    public Command getCommand() {
        return Command.MARK_VOCABULARY_ENTRY_DIFFICULT;
    }

}
