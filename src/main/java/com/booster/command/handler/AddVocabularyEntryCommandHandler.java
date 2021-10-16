package com.booster.command.handler;

import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.VocabularyEntryDao;
import com.booster.dao.WordDao;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AddVocabularyEntryCommandHandler {

    private final VocabularyEntryDao vocabularyEntryDao;
    private final WordDao wordDao;

    private final CommandLineWriter commandLineWriter;

    public void handle(CommandWithArguments commandWithArguments) {
        List<String> arguments = commandWithArguments.getArguments();

        long vocabularyId = -1;
        long wordId = -1;
        for (String arg : arguments) {
            String[] flagAndValues = arg.split("=");
            String flag = flagAndValues[0];
            String values = flagAndValues[1];

            switch (flag) {
                case "vid":
                    vocabularyId = Long.parseLong(values);
                    break;
                case "w":
                    wordId = wordDao.findByNameOrCreateAndGet(values).getId();
                    break;
                default:
                    System.out.println("ERROR");
            }
        }
        vocabularyEntryDao.add(wordId, vocabularyId);
        commandLineWriter.writeLine("Done.");
        commandLineWriter.newLine();
    }

}
