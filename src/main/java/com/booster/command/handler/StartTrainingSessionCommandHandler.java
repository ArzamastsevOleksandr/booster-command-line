package com.booster.command.handler;

import com.booster.command.Command;
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
public class StartTrainingSessionCommandHandler implements CommandHandler {

    // todo: configurable db setting
    private static final int MAX_CORRECT_ANSWERS_COUNT = 10;
    private static final int MIN_CORRECT_ANSWERS_COUNT = 0;

    private final VocabularyEntryDao vocabularyEntryDao;

    private final CommandLineWriter commandLineWriter;
    private final CommandLineReader commandLineReader;

    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        List<VocabularyEntry> vocabularyEntries = vocabularyEntryDao.findAll();
        if (vocabularyEntries.isEmpty()) {
            commandLineWriter.writeLine("There are no entries to practice.");
        } else {
            commandLineWriter.writeLine("Loaded " + vocabularyEntries.size() + " vocabulary entries.");

            for (var ve : vocabularyEntries) {
                boolean isCorrectAnswer = true;
                commandLineWriter.writeLine("Current word: [" + ve.getName() + "]");
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
                int correctAnswersCountChange = isCorrectAnswer ? 1 : -1;
                int cacUpdated = ve.getCorrectAnswersCount() + correctAnswersCountChange;
                if (isValidCorrectAnswersCount(cacUpdated)) {
                    vocabularyEntryDao.updateCorrectAnswersCount(ve.getId(), cacUpdated);
                }
            }
        }
        commandLineWriter.writeLine("Training session finished!");
        commandLineWriter.newLine();
    }

    private boolean isValidCorrectAnswersCount(int cacUpdated) {
        return MIN_CORRECT_ANSWERS_COUNT <= cacUpdated && cacUpdated <= MAX_CORRECT_ANSWERS_COUNT;
    }

    private Set<String> parseEquivalents(String equivalents) {
        return Arrays.stream(equivalents.split(";"))
                .map(String::strip)
                .collect(toSet());
    }

    @Override
    public Command getCommand() {
        return Command.START_TRAINING_SESSION;
    }

}
