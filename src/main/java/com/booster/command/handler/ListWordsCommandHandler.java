package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.WordDao;
import com.booster.model.Word;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListWordsCommandHandler implements CommandHandler {

    private final WordDao wordDao;

    private final CommandLineWriter commandLineWriter;

    // todo: default pagination + pagination flags
    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            List<Word> words = wordDao.findAll();

            if (words.isEmpty()) {
                commandLineWriter.writeLine("There are no words in the system yet.");
            } else {
                commandLineWriter.writeLine("All words:");
                for (var word : words) {
                    commandLineWriter.writeLine(word.toString());
                }
            }
        } else {
            commandLineWriter.writeLine("Errors: ");
            commandLineWriter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(commandLineWriter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_WORDS;
    }

}
