package booster.command.handler;

import booster.adapter.CommandLineAdapter;
import booster.command.Command;
import booster.command.arguments.CommandArgs;
import booster.command.arguments.StartTrainingSessionCommandArgs;
import booster.command.arguments.TrainingSessionMode;
import booster.model.VocabularyEntry;
import booster.service.VocabularyEntryService;
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

    // todo: configurable setting
    private static final int ENTRIES_PER_TRAINING_SESSION = 10;

    // Do not use in a multi-threaded environment
    private final Set<VocabularyEntry> wrongAnswers = new HashSet<>();

    private final VocabularyEntryService vocabularyEntryService;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        wrongAnswers.clear();
        var args = (StartTrainingSessionCommandArgs) commandArgs;
        executeTrainingSession(args.mode());
    }

    @Override
    public Command getCommand() {
        return Command.START_TRAINING_SESSION;
    }

    private void executeTrainingSession(TrainingSessionMode mode) {
        List<VocabularyEntry> entries = findAllForMode(mode);
        adapter.writeLine("Loaded " + entries.size() + " vocabulary entries.");
        executeTrainingSessionBasedOnMode(mode, entries);
        displayWrongAnswers();
        adapter.writeLine("Training session finished!");
    }

    private void displayWrongAnswers() {
        if (!wrongAnswers.isEmpty()) {
            adapter.writeLine("*************************************************");
            adapter.writeLine("Wrong answers:");
            adapter.newLine();
            wrongAnswers.forEach(adapter::writeLine);
            adapter.writeLine("*************************************************");
            adapter.newLine();
        }
    }

    private List<VocabularyEntry> findAllForMode(TrainingSessionMode mode) {
        return switch (mode) {
            case FULL -> vocabularyEntryService.findAllWithAntonymsAndSynonyms(ENTRIES_PER_TRAINING_SESSION);
            case SYNONYMS -> vocabularyEntryService.findAllWithSynonyms(ENTRIES_PER_TRAINING_SESSION);
            case ANTONYMS -> vocabularyEntryService.findAllWithAntonyms(ENTRIES_PER_TRAINING_SESSION);
            default -> throw new RuntimeException("Unrecognized mode: " + mode);
        };
    }

    private void executeTrainingSessionBasedOnMode(TrainingSessionMode mode, List<VocabularyEntry> entries) {
        switch (mode) {
            case FULL -> executeFullTrainingSession(entries);
            case SYNONYMS -> executeSynonymsTrainingSession(entries);
            case ANTONYMS -> executeAntonymsTrainingSession(entries);
            case UNRECOGNIZED -> throw new RuntimeException("Unrecognized training session mode");
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
        vocabularyEntryService.updateLastSeenAtById(vocabularyEntry.getId());
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
            vocabularyEntryService.updateCorrectAnswersCount(entry, true);
            adapter.writeLine("Correct!");
        } else {
            Set<String> synonymsAnswerCopy = new HashSet<>(synonymsAnswer);
            synonymsAnswerCopy.removeAll(entry.getSynonyms());

            if (synonymsAnswerCopy.isEmpty()) {
                HashSet<String> originalSynonymsCopy = new HashSet<>(entry.getSynonyms());
                originalSynonymsCopy.removeAll(synonymsAnswer);
                vocabularyEntryService.updateCorrectAnswersCount(entry, true);
                adapter.writeLine("Correct. Other synonyms: " + originalSynonymsCopy);
            } else {
                vocabularyEntryService.updateCorrectAnswersCount(entry, false);
                adapter.writeLine("Wrong. Answer is: " + entry.getSynonyms());
                wrongAnswers.add(entry);
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
            vocabularyEntryService.updateCorrectAnswersCount(entry, true);
            adapter.writeLine("Correct!");
        } else {
            Set<String> antonymsAnswerCopy = new HashSet<>(antonymsAnswer);
            antonymsAnswerCopy.removeAll(entry.getAntonyms());

            if (antonymsAnswerCopy.isEmpty()) {
                HashSet<String> originalAntonymsCopy = new HashSet<>(entry.getAntonyms());
                originalAntonymsCopy.removeAll(antonymsAnswer);
                vocabularyEntryService.updateCorrectAnswersCount(entry, true);
                adapter.writeLine("Correct. Other antonyms: " + originalAntonymsCopy);
            } else {
                vocabularyEntryService.updateCorrectAnswersCount(entry, false);
                adapter.writeLine("Wrong. Answer is: " + entry.getAntonyms());
                wrongAnswers.add(entry);
            }
        }
        adapter.newLine();
    }


    private Set<String> parseEquivalents(String equivalents) {
        return Arrays.stream(equivalents.split(";"))
                .map(String::strip)
                .collect(toSet());
    }

}
