package com.booster.command.handler;

import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.DeleteVocabularyArgs;
import com.booster.dao.VocabularyDao;
import com.booster.dao.VocabularyEntryDao;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteVocabularyCommandHandler {

    private final VocabularyDao vocabularyDao;
    private final VocabularyEntryDao vocabularyEntryDao;

    private final CommandLineWriter commandLineWriter;

    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            var args = (DeleteVocabularyArgs) commandWithArguments.getArgs();
            vocabularyEntryDao.deleteAllForVocabularyId(args.getId());
            vocabularyDao.delete(args.getId());
            commandLineWriter.writeLine("Done.");
        } else {
            commandLineWriter.writeLine("Errors: ");
            commandLineWriter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(commandLineWriter::writeLine);
        }
        commandLineWriter.newLine();
    }

}
