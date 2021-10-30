package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.MarkVocabularyEntryNotDifficultArgs;
import com.booster.dao.VocabularyEntryDao;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarkVocabularyEntryNotDifficultCommandHandler implements CommandHandler {

    private final VocabularyEntryDao vocabularyEntryDao;

    private final CommandLineWriter commandLineWriter;

    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            var args = (MarkVocabularyEntryNotDifficultArgs) commandWithArguments.getArgs();
            vocabularyEntryDao.markDifficult(args.getId(), false);
            commandLineWriter.writeLine("Done.");
        } else {
            commandLineWriter.writeLine("Errors: ");
            commandLineWriter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(commandLineWriter::writeLine);
        }
        commandLineWriter.newLine();
    }

    @Override
    public Command getCommand() {
        return Command.MARK_VOCABULARY_ENTRY_NOT_DIFFICULT;
    }

}
