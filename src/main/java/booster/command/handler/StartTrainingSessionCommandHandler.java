package booster.command.handler;

import booster.adapter.CommandLineAdapter;
import booster.command.Command;
import booster.command.arguments.CommandArgs;
import booster.command.arguments.StartTrainingSessionCommandArgs;
import booster.command.arguments.TrainingSessionMode;
import booster.model.VocabularyEntry;
import booster.service.VocabularyEntryService;
import booster.util.ColorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
public class StartTrainingSessionCommandHandler implements CommandHandler {

    // todo: configurable setting
    private static final int ENTRIES_PER_TRAINING_SESSION = 5;

    private final VocabularyEntryService vocabularyEntryService;
    private final CommandLineAdapter adapter;
    private final TrainingSessionStats stats;

    @Override
    public void handle(CommandArgs commandArgs) {
        stats.reset();
        var args = (StartTrainingSessionCommandArgs) commandArgs;
        executeTrainingSession(args.mode());
    }

    @Override
    public Command getCommand() {
        return Command.START_TRAINING_SESSION;
    }

    private void executeTrainingSession(TrainingSessionMode mode) {
        List<VocabularyEntry> entries = findAllForMode(mode);
        adapter.writeLine("Loaded " + ColorCodes.cyan(entries.size()) + " entries.");
        executeTrainingSessionBasedOnMode(mode, entries);
        stats.displayAnswers();
        adapter.writeLine(ColorCodes.yellow("Training session finished!"));
    }

    private List<VocabularyEntry> findAllForMode(TrainingSessionMode mode) {
        return switch (mode) {
            case FULL -> vocabularyEntryService.findAllWithAntonymsAndSynonyms(ENTRIES_PER_TRAINING_SESSION);
            case SYNONYMS -> vocabularyEntryService.findAllWithSynonyms(ENTRIES_PER_TRAINING_SESSION);
            case ANTONYMS -> vocabularyEntryService.findAllWithAntonyms(ENTRIES_PER_TRAINING_SESSION);
            default -> throw new RuntimeException("Unrecognized training session mode: " + mode);
        };
    }

    private void executeTrainingSessionBasedOnMode(TrainingSessionMode mode, List<VocabularyEntry> entries) {
        switch (mode) {
            case FULL -> executeFullTrainingSession(entries);
            case SYNONYMS -> executeSynonymsTrainingSession(entries);
            case ANTONYMS -> executeAntonymsTrainingSession(entries);
            case UNRECOGNIZED -> throw new RuntimeException("Unrecognized training session mode: " + mode);
        }
    }

    // todo: DRY
    private void executeFullTrainingSession(List<VocabularyEntry> entries) {
        int index = 0;

        VocabularyEntry entry = fetchNextAndPrint(entries, index);
        String enteredSynonyms = readSynonyms();

        while (!enteredSynonyms.equalsIgnoreCase("e") && index++ < entries.size()) {
            Set<String> synonymsAnswer = parseEquivalents(enteredSynonyms);
            handleAnswerSynonyms(synonymsAnswer, entry);

            String enteredAntonyms = readAntonyms();
            Set<String> antonymsAnswer = parseEquivalents(enteredAntonyms);
            handleAnswerAntonyms(antonymsAnswer, entry);
            if (index < entries.size()) {
                entry = fetchNextAndPrint(entries, index);
                enteredSynonyms = readSynonyms();
            }
        }
    }

    private VocabularyEntry fetchNextAndPrint(List<VocabularyEntry> entries, int index) {
        VocabularyEntry entry = entries.get(index);
        printCurrentWord(entry);
        return entry;
    }

    private String readSynonyms() {
        return readEquivalents("Synonyms");
    }

    private String readAntonyms() {
        return readEquivalents("Antonyms");
    }

    private String readEquivalents(String label) {
        adapter.write(label + " >> ");
        return adapter.readLine();
    }

    private void printCurrentWord(VocabularyEntry entry) {
        adapter.writeLine("Word: [" + ColorCodes.cyan(entry.getName()) + "]");
        adapter.newLine();
        vocabularyEntryService.updateLastSeenAtById(entry.getId());
    }

    private void executeSynonymsTrainingSession(List<VocabularyEntry> entries) {
        int index = 0;

        VocabularyEntry entry = fetchNextAndPrint(entries, index);
        String enteredSynonyms = readSynonyms();

        while (!enteredSynonyms.equalsIgnoreCase("e") && index++ < entries.size()) {
            Set<String> synonymsAnswer = parseEquivalents(enteredSynonyms);
            handleAnswerSynonyms(synonymsAnswer, entry);
            if (index < entries.size()) {
                entry = fetchNextAndPrint(entries, index);
                enteredSynonyms = readSynonyms();
            }
        }
    }

    private void handleAnswerSynonyms(Set<String> synonymsAnswer, VocabularyEntry entry) {
        if (synonymsAnswer.equals(entry.getSynonyms())) {
            processCorrectAnswer(entry);
        } else {
            Set<String> synonymsAnswerCopy = new HashSet<>(synonymsAnswer);
            synonymsAnswerCopy.removeAll(entry.getSynonyms());

            if (synonymsAnswerCopy.isEmpty()) {
                processPartialSynonymsAnswer(synonymsAnswer, entry);
            } else {
                processWrongAnswer(entry, entry::getSynonyms);
            }
        }
        adapter.newLine();
    }

    private void processPartialSynonymsAnswer(Set<String> partialAnswer, VocabularyEntry entry) {
        processPartialAnswer(partialAnswer, entry, entry::getSynonyms, "synonyms");
    }

    private void processPartialAntonymsAnswer(Set<String> partialAnswer, VocabularyEntry entry) {
        processPartialAnswer(partialAnswer, entry, entry::getAntonyms, "antonyms");
    }

    private void processPartialAnswer(Set<String> partialAnswer, VocabularyEntry entry, Supplier<Set<String>> supplier, String label) {
        Set<String> originalEquivalentsCopy = new HashSet<>(supplier.get());
        originalEquivalentsCopy.removeAll(partialAnswer);
        vocabularyEntryService.updateCorrectAnswersCount(entry, true);
        adapter.writeLine(ColorCodes.yellow("Correct."));
        adapter.writeLine("Other " + label + ": " + ColorCodes.yellow(String.join(", ", originalEquivalentsCopy)));
        stats.addPartialAnswer(entry);
    }

    private void processWrongAnswer(VocabularyEntry entry, Supplier<Set<String>> supplier) {
        vocabularyEntryService.updateCorrectAnswersCount(entry, false);
        adapter.writeLine(ColorCodes.red("Wrong."));
        adapter.writeLine("Answer is: " + ColorCodes.red(String.join(", ", supplier.get())));
        stats.addWrongAnswer(entry);
    }

    private void processCorrectAnswer(VocabularyEntry entry) {
        vocabularyEntryService.updateCorrectAnswersCount(entry, true);
        adapter.writeLine(ColorCodes.green("Correct!"));
        stats.addCorrectAnswer(entry);
    }

    private void executeAntonymsTrainingSession(List<VocabularyEntry> entries) {
        int index = 0;

        VocabularyEntry entry = fetchNextAndPrint(entries, index);
        String enteredAntonyms = readAntonyms();

        while (!enteredAntonyms.equalsIgnoreCase("e") && index++ < entries.size()) {
            Set<String> antonymsAnswer = parseEquivalents(enteredAntonyms);
            handleAnswerAntonyms(antonymsAnswer, entry);
            if (index < entries.size()) {
                entry = fetchNextAndPrint(entries, index);
                enteredAntonyms = readAntonyms();
            }
        }
    }

    private void handleAnswerAntonyms(Set<String> antonymsAnswer, VocabularyEntry entry) {
        if (antonymsAnswer.equals(entry.getAntonyms())) {
            processCorrectAnswer(entry);
        } else {
            Set<String> antonymsAnswerCopy = new HashSet<>(antonymsAnswer);
            antonymsAnswerCopy.removeAll(entry.getAntonyms());

            if (antonymsAnswerCopy.isEmpty()) {
                processPartialAntonymsAnswer(antonymsAnswer, entry);
            } else {
                processWrongAnswer(entry, entry::getAntonyms);
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
