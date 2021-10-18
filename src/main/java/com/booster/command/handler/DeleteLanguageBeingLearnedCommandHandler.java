package com.booster.command.handler;

import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.DeleteLanguageBeingLearnedArgs;
import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteLanguageBeingLearnedCommandHandler {

    private final LanguageBeingLearnedDao languageBeingLearnedDao;

    private final CommandLineWriter commandLineWriter;

    // todo: if the v exists for this lbl - foreign key constraint "vocabulary_language_being_learned_id_fkey" is fired
    // todo: if no record deleted - notify the user
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            var args = (DeleteLanguageBeingLearnedArgs) commandWithArguments.getArgs();
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
