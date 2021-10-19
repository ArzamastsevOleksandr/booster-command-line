package com.booster.command.handler;

import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.DeleteLanguageBeingLearnedArgs;
import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.dao.VocabularyDao;
import com.booster.dao.VocabularyEntryDao;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteLanguageBeingLearnedCommandHandler {

    private final LanguageBeingLearnedDao languageBeingLearnedDao;
    private final VocabularyDao vocabularyDao;
    private final VocabularyEntryDao vocabularyEntryDao;

    private final CommandLineWriter commandLineWriter;

    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            var args = (DeleteLanguageBeingLearnedArgs) commandWithArguments.getArgs();
            vocabularyDao.findAllIdsForLanguageBeingLearnedId(args.getId()).forEach(vocabularyId -> {
                vocabularyEntryDao.deleteAllForVocabularyId(vocabularyId);
                vocabularyDao.delete(vocabularyId);
            });
            languageBeingLearnedDao.delete(args.getId());
            commandLineWriter.writeLine("Done.");
        } else {
            commandLineWriter.writeLine("Errors: ");
            commandLineWriter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(commandLineWriter::writeLine);
        }
    }

}
