package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.StartTrainingSessionCommandArgs;
import cliclient.command.arguments.TrainingSessionMode;
import cliclient.model.VocabularyEntry;
import cliclient.service.VocabularyEntryService;
import cliclient.util.ColorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
public class StartTrainingSessionCommandHandler implements CommandHandler {

    // todo: configurable setting
    private static final int ENTRIES_PER_TRAINING_SESSION = 5;

    private final VocabularyEntryService vocabularyEntryService;
    private final CommandLineAdapter adapter;
    private final TrainingSessionStats stats;
    private final ColorProcessor colorProcessor;

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

    @RequiredArgsConstructor
    private class EntryTracker {
        // todo: configurable setting
        final int maxHintsPerEntry = 3;

        TrainingSessionMode mode = TrainingSessionMode.getDefaultMode();
        int index = 0;
        int hintsPerEntryUsed = 0;
        VocabularyEntry current;
        final List<VocabularyEntry> entries;

        boolean shouldContinue(String answer) {
            return !"e".equalsIgnoreCase(answer) && hasMoreEntries();
        }

        boolean hasMoreEntries() {
            return index < entries.size();
        }

        VocabularyEntry fetchNextAndPrint() {
            current = entries.get(index);
            printCurrentWord(current);
            return current;
        }

        void printCurrentWord(VocabularyEntry entry) {
            adapter.writeLine("Word: " + ColorCodes.cyan(entry.getName()));
            adapter.newLine();
            vocabularyEntryService.updateLastSeenAtById(entry.getId());
        }

        void inc() {
            index++;
            hintsPerEntryUsed = 0;
        }

        boolean canShowHints(String input) {
            return "h".equalsIgnoreCase(input);
        }

        String hint() {
            hintsPerEntryUsed++;
            return getCorrectAnswers().stream()
                    .map(s -> s.substring(0, hintsPerEntryUsed) + "_".repeat(s.length() - hintsPerEntryUsed))
                    .collect(Collectors.joining(";"));
        }

        Set<String> getCorrectAnswers() {
            return switch (mode) {
                case SYNONYMS -> current.getSynonyms();
                case ANTONYMS -> current.getAntonyms();
                case UNRECOGNIZED -> throw new RuntimeException("Unrecognized tracker mode. Unable to get correct answers.");
            };
        }

        boolean allHintsExhausted() {
            return hintsPerEntryUsed > maxHintsPerEntry;
        }

        EntryTracker modeSynonyms() {
            mode = TrainingSessionMode.SYNONYMS;
            return this;
        }

        EntryTracker modeAntonyms() {
            mode = TrainingSessionMode.ANTONYMS;
            return this;
        }
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
            case SYNONYMS -> vocabularyEntryService.findAllWithSynonyms(ENTRIES_PER_TRAINING_SESSION);
            case ANTONYMS -> vocabularyEntryService.findAllWithAntonyms(ENTRIES_PER_TRAINING_SESSION);
            default -> throw new RuntimeException("Unrecognized training session mode: " + mode);
        };
    }

    private void executeTrainingSessionBasedOnMode(TrainingSessionMode mode, List<VocabularyEntry> entries) {
        var tracker = new EntryTracker(entries);
        switch (mode) {
            case SYNONYMS -> executeSynonymsTrainingSession(tracker.modeSynonyms());
            case ANTONYMS -> executeAntonymsTrainingSession(tracker.modeAntonyms());
            case UNRECOGNIZED -> throw new RuntimeException("Unrecognized training session mode: " + mode);
        }
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

    private void executeSynonymsTrainingSession(EntryTracker tracker) {
        executeTrainingSession(tracker, this::readSynonyms, this::handleAnswerSynonyms);
    }

    private void executeAntonymsTrainingSession(EntryTracker tracker) {
        executeTrainingSession(tracker, this::readAntonyms, this::handleAnswerAntonyms);
    }

    private void executeTrainingSession(EntryTracker tracker,
                                        Supplier<String> answerSupplier,
                                        BiConsumer<Set<String>, VocabularyEntry> answerConsumer) {
        VocabularyEntry entry = tracker.fetchNextAndPrint();
        String answer = answerSupplier.get();

        while (tracker.shouldContinue(answer)) {
            tracker.inc();
            while (tracker.canShowHints(answer)) {
                adapter.writeLine("Hint: >> " + tracker.hint());
                if (tracker.allHintsExhausted()) {
                    break;
                }
                answer = answerSupplier.get();
            }
            if (tracker.allHintsExhausted()) {
                adapter.writeLine(ColorCodes.red("Max hints used."));
                stats.skipped(tracker.current);
                adapter.writeLine(colorProcessor.coloredEntry(tracker.current));
                ThreadUtil.sleepSeconds(1);
            } else {
                Set<String> parsedAnswer = parseEquivalents(answer);
                answerConsumer.accept(parsedAnswer, entry);
            }
            if (tracker.hasMoreEntries()) {
                entry = tracker.fetchNextAndPrint();
                answer = answerSupplier.get();
            }
        }
    }

    private void handleAnswerSynonyms(Set<String> synonymsAnswer, VocabularyEntry entry) {
        handleAnswer(synonymsAnswer, entry, entry::getSynonyms, this::processPartialSynonymsAnswer);
    }

    private void handleAnswerAntonyms(Set<String> antonymsAnswer, VocabularyEntry entry) {
        handleAnswer(antonymsAnswer, entry, entry::getAntonyms, this::processPartialAntonymsAnswer);
    }

    private void handleAnswer(Set<String> answer,
                              VocabularyEntry entry,
                              Supplier<Set<String>> correctAnswer,
                              BiConsumer<Set<String>, VocabularyEntry> consumer) {
        if (answer.equals(correctAnswer.get())) {
            processCorrectAnswer(entry);
        } else {
            Set<String> answerCopy = new HashSet<>(answer);
            answerCopy.removeAll(correctAnswer.get());

            if (answerCopy.isEmpty()) {
                consumer.accept(answer, entry);
            } else {
                processWrongAnswer(entry, correctAnswer);
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

    private void processPartialAnswer(Set<String> partialAnswer,
                                      VocabularyEntry entry,
                                      Supplier<Set<String>> supplier,
                                      String label) {
        Set<String> originalEquivalentsCopy = new HashSet<>(supplier.get());
        originalEquivalentsCopy.removeAll(partialAnswer);
        vocabularyEntryService.incCorrectAnswersCount(entry);
        adapter.writeLine(ColorCodes.yellow("Correct."));
        adapter.writeLine("Other " + label + ": " + ColorCodes.yellow(String.join(", ", originalEquivalentsCopy)));
        stats.addPartialAnswer(entry);
    }

    private void processWrongAnswer(VocabularyEntry entry, Supplier<Set<String>> supplier) {
        vocabularyEntryService.decCorrectAnswersCount(entry);
        adapter.writeLine(ColorCodes.red("Wrong."));
        adapter.writeLine("Answer is: " + ColorCodes.red(String.join(", ", supplier.get())));
        stats.addWrongAnswer(entry);
    }

    private void processCorrectAnswer(VocabularyEntry entry) {
        vocabularyEntryService.incCorrectAnswersCount(entry);
        adapter.writeLine(ColorCodes.green("Correct!"));
        stats.addCorrectAnswer(entry);
    }

    private Set<String> parseEquivalents(String equivalents) {
        return Arrays.stream(equivalents.split(";"))
                .map(String::strip)
                .collect(toSet());
    }

}
