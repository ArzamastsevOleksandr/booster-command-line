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

    private static final int MIN_CORRECT_ANSWERS_COUNT = 0;
    // todo: configurable setting
    private static final int ENTRIES_PER_TRAINING_SESSION = 10;

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
                return vocabularyEntryDao.findAllWithAntonymsAndSynonyms(ENTRIES_PER_TRAINING_SESSION);
            case SYNONYMS:
                return vocabularyEntryDao.findAllWithSynonyms(ENTRIES_PER_TRAINING_SESSION);
            case ANTONYMS:
                return vocabularyEntryDao.findAllWithAntonyms(ENTRIES_PER_TRAINING_SESSION);
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

    // todo: DRY
    private void executeFullTrainingSession(List<VocabularyEntry> vocabularyEntries) {
        int index = 0;

        VocabularyEntry entry = vocabularyEntries.get(index);
        printCurrentWord(entry);
        adapter.write("Synonyms >> ");
        String enteredSynonyms = adapter.readLine();

        while (!enteredSynonyms.equalsIgnoreCase("e") && index++ < vocabularyEntries.size()) {
            Set<String> synonymsAnswer = parseEquivalents(enteredSynonyms);
            handleAnswerSynonyms(synonymsAnswer, entry);

            adapter.write("Antonyms >> ");
            String enteredAntonyms = adapter.readLine();

            Set<String> antonymsAnswer = parseEquivalents(enteredAntonyms);
            handleAnswerAntonyms(antonymsAnswer, entry);

            if (index < vocabularyEntries.size()) {
                entry = vocabularyEntries.get(index);
                printCurrentWord(entry);
                adapter.write("Synonyms >> ");
                enteredSynonyms = adapter.readLine();
            }
        }
    }

    private void printCurrentWord(VocabularyEntry vocabularyEntry) {
        adapter.writeLine("Current word: [" + vocabularyEntry.getName() + "]");
        adapter.newLine();
    }

    private void executeSynonymsTrainingSession(List<VocabularyEntry> vocabularyEntries) {
        int index = 0;

        VocabularyEntry entry = vocabularyEntries.get(index);
        printCurrentWord(entry);
        adapter.write("Synonyms >> ");
        String enteredSynonyms = adapter.readLine();

        while (!enteredSynonyms.equalsIgnoreCase("e") && index++ < vocabularyEntries.size()) {
            Set<String> synonymsAnswer = parseEquivalents(enteredSynonyms);
            handleAnswerSynonyms(synonymsAnswer, entry);

            if (index < vocabularyEntries.size()) {
                entry = vocabularyEntries.get(index);
                printCurrentWord(entry);
                adapter.write("Synonyms >> ");
                enteredSynonyms = adapter.readLine();
            }
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
        int index = 0;

        VocabularyEntry entry = vocabularyEntries.get(index);
        printCurrentWord(entry);
        adapter.write("Antonyms >> ");
        String enteredAntonyms = adapter.readLine();

        while (!enteredAntonyms.equalsIgnoreCase("e") && index++ < vocabularyEntries.size()) {
            Set<String> antonymsAnswer = parseEquivalents(enteredAntonyms);
            handleAnswerAntonyms(antonymsAnswer, entry);

            if (index < vocabularyEntries.size()) {
                entry = vocabularyEntries.get(index);
                printCurrentWord(entry);
                adapter.write("Antonyms >> ");
                enteredAntonyms = adapter.readLine();
            }
        }
    }

    private void handleAnswerAntonyms(Set<String> antonymsAnswer, VocabularyEntry entry) {
        if (antonymsAnswer.equals(entry.getAntonyms())) {
            updateCorrectAnswersCount(entry, true);
            adapter.writeLine("Correct!");
        } else {
            Set<String> antonymsAnswerCopy = new HashSet<>(antonymsAnswer);
            antonymsAnswerCopy.removeAll(entry.getAntonyms());

            if (antonymsAnswerCopy.isEmpty()) {
                HashSet<String> originalAntonymsCopy = new HashSet<>(entry.getAntonyms());
                originalAntonymsCopy.removeAll(antonymsAnswer);
                updateCorrectAnswersCount(entry, true);
                adapter.writeLine("Correct. Other antonyms: " + originalAntonymsCopy);
            } else {
                updateCorrectAnswersCount(entry, false);
                adapter.writeLine("Wrong. Answer is: " + entry.getAntonyms());
            }
        }
        adapter.newLine();
    }

    private void updateCorrectAnswersCount(VocabularyEntry ve, boolean isCorrectAnswer) {
        int correctAnswersCountChange = isCorrectAnswer ? 1 : -1;
        int cacUpdated = ve.getCorrectAnswersCount() + correctAnswersCountChange;
        if (isValidCorrectAnswersCount(cacUpdated)) {
            vocabularyEntryDao.updateCorrectAnswersCount(ve.getId(), cacUpdated);
        }
    }

    private boolean isValidCorrectAnswersCount(int cacUpdated) {
        return MIN_CORRECT_ANSWERS_COUNT <= cacUpdated;
    }

    private Set<String> parseEquivalents(String equivalents) {
        return Arrays.stream(equivalents.split(";"))
                .map(String::strip)
                .collect(toSet());
    }

}
