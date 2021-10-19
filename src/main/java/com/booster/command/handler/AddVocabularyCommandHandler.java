package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.AddVocabularyArgs;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.VocabularyDao;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddVocabularyCommandHandler implements CommandHandler {

    private final VocabularyDao vocabularyDao;

    private final CommandLineWriter commandLineWriter;

    // todo: a common handler interface with a template for actions. Child classes override an abstract action-method.
    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            var args = (AddVocabularyArgs) commandWithArguments.getArgs();
            vocabularyDao.add(args.getName(), args.getLanguageBeingLearnedId());
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
        return Command.ADD_VOCABULARY;
    }

}
