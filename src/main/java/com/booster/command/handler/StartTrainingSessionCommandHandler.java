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
import java.util.HashSet;
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
        commandWithArgs.getMode().ifPresent(this::executeTrainingSession);
    }

    @Override
    public Command getCommand() {
        return Command.START_TRAINING_SESSION;
    }

    private void executeTrainingSession(TrainingSessionMode mode) {
        List<VocabularyEntry> entries = findAllForMode(mode);
        adapter.writeLine("Loaded " + entries.size() + " vocabulary entries.");
        executeTrainingSessionBasedOnMode(mode, entries);
        adapter.writeLine("Training session finished!");
    }

    private List<VocabularyEntry> findAllForMode(TrainingSessionMode mode) {
        switch (mode) {
            case FULL:
                return vocabularyEntryDao.findAllWithAntonymsAndSynonyms();
            case SYNONYMS:
                return vocabularyEntryDao.findAllWithSynonyms();
            case ANTONYMS:
                return vocabularyEntryDao.findAllWithAntonyms();
            default:
                throw new RuntimeException("Unrecognized mode: " + mode);
        }
    }

    private void executeTrainingSessionBasedOnMode(TrainingSessionMode mode, List<VocabularyEntry> entries) {
        switch (mode) {
            case FULL:
                executeFullTrainingSession(entries);
                break;
            case SYNONYMS:
                executeSynonymsTrainingSession(entries);
                break;
            case ANTONYMS:
                executeAntonymsTrainingSession(entries);
                break;
        }
    }

    private void executeFullTrainingSession(List<VocabularyEntry> vocabularyEntries) {
        for (var vocabularyEntry : vocabularyEntries) {
            printCurrentWord(vocabularyEntry);

            boolean isCorrectAnswer = checkSynonyms(vocabularyEntry);
            handleAnswerSynonyms(isCorrectAnswer);

            if (isCorrectAnswer) {
                isCorrectAnswer = checkAntonyms(vocabularyEntry);
                handleAnswerSynonyms(isCorrectAnswer);
            }
            updateCorrectAnswersCount(vocabularyEntry, isCorrectAnswer);
        }
    }

    private void printCurrentWord(VocabularyEntry vocabularyEntry) {
        adapter.writeLine("Current word: [" + vocabularyEntry.getName() + "]");
        adapter.newLine();
    }

    private boolean checkSynonyms(VocabularyEntry ve) {
        adapter.write("Synonyms >> ");
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

    private void handleAnswerSynonyms(boolean isCorrectAnswer) {
        if (isCorrectAnswer) {
            adapter.writeLine("Correct!");
        } else {
            adapter.writeLine("Wrong!");
        }
        adapter.newLine();
    }

    private void executeSynonymsTrainingSession(List<VocabularyEntry> vocabularyEntries) {
        int index = 0;

        VocabularyEntry entry = vocabularyEntries.get(index++);
        printCurrentWord(entry);
        adapter.write("Synonyms >> ");
        String enteredSynonyms = adapter.readLine();

        while (!enteredSynonyms.equalsIgnoreCase("e") && index < vocabularyEntries.size()) {
            Set<String> synonymsAnswer = parseEquivalents(enteredSynonyms);
            handleAnswerSynonyms(synonymsAnswer, entry);

            entry = vocabularyEntries.get(index++);
            printCurrentWord(entry);
            adapter.write("Synonyms >> ");
            enteredSynonyms = adapter.readLine();
        }
    }

    private void handleAnswerSynonyms(Set<String> synonymsAnswer, VocabularyEntry entry) {
        if (synonymsAnswer.equals(entry.getSynonyms())) {
            updateCorrectAnswersCount(entry, true);
            adapter.writeLine("Correct!");
        } else {
            Set<String> synonymsAnswerCopy = new HashSet<>(synonymsAnswer);
            synonymsAnswerCopy.removeAll(entry.getSynonyms());

            if (synonymsAnswerCopy.isEmpty()) {
                HashSet<String> originalSynonymsCopy = new HashSet<>(entry.getSynonyms());
                originalSynonymsCopy.removeAll(synonymsAnswer);
                updateCorrectAnswersCount(entry, true);
                adapter.writeLine("Correct. Other synonyms: " + originalSynonymsCopy);
            } else {
                updateCorrectAnswersCount(entry, false);
                adapter.writeLine("Wrong. Answer is: " + entry.getSynonyms());
            }
        }
        adapter.newLine();
    }

    private void executeAntonymsTrainingSession(List<VocabularyEntry> vocabularyEntries) {
        for (var vocabularyEntry : vocabularyEntries) {
            printCurrentWord(vocabularyEntry);

            boolean isCorrectAnswer = checkAntonyms(vocabularyEntry);
            handleAnswerSynonyms(isCorrectAnswer);

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
