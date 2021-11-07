package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.command.arguments.TrainingSessionMode;
import com.booster.dao.VocabularyEntryDao;
import com.booster.model.VocabularyEntry;
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

    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getMode()
                .ifPresent(mode -> {
                    List<VocabularyEntry> vocabularyEntries = findAllForMode(mode);
                    adapter.writeLine("Loaded " + vocabularyEntries.size() + " vocabulary entries.");
                    executeTrainingSession(vocabularyEntries, mode);
                    adapter.writeLine("Training session finished!");
                });
    }

    @Override
    public Command getCommand() {
        return Command.START_TRAINING_SESSION;
    }

    private List<VocabularyEntry> findAllForMode(TrainingSessionMode mode) {
        switch (mode) {
            case FULL:
                return vocabularyEntryDao.findAll();
            case SYNONYMS:
                return vocabularyEntryDao.findAllWithSynonyms();
            case ANTONYMS:
                return vocabularyEntryDao.findAllWithAntonyms();
            default:
                return List.of();
        }
    }

    private void executeTrainingSession(List<VocabularyEntry> vocabularyEntries, TrainingSessionMode mode) {
        if (mode == TrainingSessionMode.FULL) {
            executeFullTrainingSession(vocabularyEntries);
        } else if (mode == TrainingSessionMode.SYNONYMS) {
            executeSynonymsTrainingSession(vocabularyEntries);
        } else {
            executeAntonymsTrainingSession(vocabularyEntries);
        }
    }

    private void executeFullTrainingSession(List<VocabularyEntry> vocabularyEntries) {
        for (var vocabularyEntry : vocabularyEntries) {
            printCurrentWord(vocabularyEntry);

            boolean isCorrectAnswer = checkSynonyms(vocabularyEntry);
            handleAnswer(isCorrectAnswer);

            if (isCorrectAnswer) {
                isCorrectAnswer = checkAntonyms(vocabularyEntry);
                handleAnswer(isCorrectAnswer);
            }
            updateCorrectAnswersCount(vocabularyEntry, isCorrectAnswer);
        }
    }

    private void printCurrentWord(VocabularyEntry vocabularyEntry) {
        adapter.writeLine("Current word: [" + vocabularyEntry.getName() + "]");
        adapter.newLine();
    }

    private boolean checkSynonyms(VocabularyEntry ve) {
        adapter.write("Enter synonyms: ");
        String enteredSynonyms = adapter.readLine();
        Set<String> synonymsAnswer = parseEquivalents(enteredSynonyms);

        return synonymsAnswer.equals(ve.getSynonyms());
    }

    private boolean checkAntonyms(VocabularyEntry ve) {
        adapter.write("Enter antonyms: ");
        String enteredAntonyms = adapter.readLine();
        Set<String> antonymsAnswer = parseEquivalents(enteredAntonyms);

        return antonymsAnswer.equals(ve.getAntonyms());
    }

    private void handleAnswer(boolean isCorrectAnswer) {
        if (isCorrectAnswer) {
            adapter.writeLine("Correct!");
        } else {
            adapter.writeLine("Wrong!");
        }
        adapter.newLine();
    }

    private void executeSynonymsTrainingSession(List<VocabularyEntry> vocabularyEntries) {
        for (var vocabularyEntry : vocabularyEntries) {
            printCurrentWord(vocabularyEntry);

            boolean isCorrectAnswer = checkSynonyms(vocabularyEntry);
            handleAnswer(isCorrectAnswer);

            updateCorrectAnswersCount(vocabularyEntry, isCorrectAnswer);
        }
    }

    private void executeAntonymsTrainingSession(List<VocabularyEntry> vocabularyEntries) {
        for (var vocabularyEntry : vocabularyEntries) {
            printCurrentWord(vocabularyEntry);

            boolean isCorrectAnswer = checkAntonyms(vocabularyEntry);
            handleAnswer(isCorrectAnswer);

            updateCorrectAnswersCount(vocabularyEntry, isCorrectAnswer);
        }
    }

    private void updateCorrectAnswersCount(VocabularyEntry ve, boolean isCorrectAnswer) {
        int correctAnswersCountChange = isCorrectAnswer ? 1 : -1;
        int cacUpdated = ve.getCorrectAnswersCount() + correctAnswersCountChange;
        if (isValidCorrectAnswersCount(cacUpdated)) {
            vocabularyEntryDao.updateCorrectAnswersCount(ve.getId(), cacUpdated);
        }
    }

    private boolean isValidCorrectAnswersCount(int cacUpdated) {
        return MIN_CORRECT_ANSWERS_COUNT <= cacUpdated && cacUpdated <= MAX_CORRECT_ANSWERS_COUNT;
    }

    private Set<String> parseEquivalents(String equivalents) {
        return Arrays.stream(equivalents.split(";"))
                .map(String::strip)
                .collect(toSet());
    }

}
