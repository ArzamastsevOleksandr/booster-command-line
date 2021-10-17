package com.booster.command.handler;

import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.VocabularyEntryDao;
import com.booster.input.CommandLineReader;
import com.booster.model.VocabularyEntry;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
public class StartTrainingSessionCommandHandler {

    private final VocabularyEntryDao vocabularyEntryDao;

    private final CommandLineWriter commandLineWriter;
    private final CommandLineReader commandLineReader;

    public void handle(CommandWithArguments commandWithArguments) {
        List<VocabularyEntry> vocabularyEntries = vocabularyEntryDao.findAll();
        if (vocabularyEntries.isEmpty()) {
            commandLineWriter.writeLine("There are no entries to practice.");
        } else {
            commandLineWriter.writeLine("Loaded " + vocabularyEntries.size() + " vocabulary entries.");

            for (var ve : vocabularyEntries) {
                commandLineWriter.writeLine("Word: " + ve.getWord().getName() + ".");
                commandLineWriter.write("Enter synonyms: ");
                String input = commandLineReader.readLine();
                Set<String> synonymsAnswer = Arrays.stream(input.split(";"))
                        .map(String::strip)
                        .collect(toSet());

                if (synonymsAnswer.equals(ve.getSynonyms())) {
                    commandLineWriter.writeLine("Correct.");
                } else {
                    commandLineWriter.writeLine("Wrong.");
                }
            }
        }

        commandLineWriter.writeLine("Done");
        commandLineWriter.newLine();
    }

}
