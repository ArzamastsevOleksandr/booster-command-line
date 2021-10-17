package com.booster.command.handler;

import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.VocabularyEntryDao;
import com.booster.dao.WordDao;
import com.booster.model.Word;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

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
        List<Long> synonymIds = new ArrayList<>();
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
                case "s":
                    synonymIds = getSynonymIds(values);
                    break;
                default:
                    System.out.println("ERROR");
            }
        }
        vocabularyEntryDao.add(wordId, vocabularyId, synonymIds);
        commandLineWriter.writeLine("Done.");
        commandLineWriter.newLine();
    }

    private List<Long> getSynonymIds(String values) {
        return Arrays.stream(values.split(";"))
                .map(String::strip)
                .map(wordDao::findByNameOrCreateAndGet)
                .map(Word::getId)
                .collect(toList());
    }

}
