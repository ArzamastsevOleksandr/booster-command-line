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
                boolean isCorrectAnswer = true;
                commandLineWriter.writeLine("Current word: [" + ve.getWord().getName() + "]");
                commandLineWriter.newLine();

                // todo: separate methods
                commandLineWriter.write("Enter synonyms: ");
                String enteredSynonyms = commandLineReader.readLine();
                Set<String> synonymsAnswer = parseEquivalents(enteredSynonyms);

                if (synonymsAnswer.equals(ve.getSynonyms())) {
                    commandLineWriter.writeLine("Correct!");
                } else {
                    commandLineWriter.writeLine("Wrong!");
                    isCorrectAnswer = false;
                }
                commandLineWriter.newLine();

                if (isCorrectAnswer) {
                    commandLineWriter.write("Enter antonyms: ");
                    String enteredAntonyms = commandLineReader.readLine();
                    Set<String> antonymsAnswer = parseEquivalents(enteredAntonyms);

                    if (antonymsAnswer.equals(ve.getAntonyms())) {
                        commandLineWriter.writeLine("Correct!");
                    } else {
                        commandLineWriter.writeLine("Wrong!");
                    }
                } else {
                    commandLineWriter.writeLine("Going to the next word.");
                }
                commandLineWriter.newLine();
            }
        }
        commandLineWriter.writeLine("Training session finished!");
        commandLineWriter.newLine();
    }

    private Set<String> parseEquivalents(String equivalents) {
        return Arrays.stream(equivalents.split(";"))
                .map(String::strip)
                .collect(toSet());
    }

}
