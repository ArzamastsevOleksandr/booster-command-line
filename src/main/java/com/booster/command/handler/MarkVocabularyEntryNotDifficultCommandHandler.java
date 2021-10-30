package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.MarkVocabularyEntryNotDifficultArgs;
import com.booster.dao.VocabularyEntryDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarkVocabularyEntryNotDifficultCommandHandler implements CommandHandler {

    private final VocabularyEntryDao vocabularyEntryDao;

    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            var args = (MarkVocabularyEntryNotDifficultArgs) commandWithArguments.getArgs();
            vocabularyEntryDao.markDifficult(args.getId(), false);
            adapter.writeLine("Done.");
        } else {
            adapter.writeLine("Errors: ");
            adapter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(adapter::writeLine);
        }
        adapter.newLine();
    }

    @Override
    public Command getCommand() {
        return Command.MARK_VOCABULARY_ENTRY_NOT_DIFFICULT;
    }

}
