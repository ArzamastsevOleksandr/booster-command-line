package cliclient.command.handler;

import api.vocabulary.PatchVocabularyEntryInput;
import api.vocabulary.VocabularyEntryApi;
import api.vocabulary.VocabularyEntryDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.args.CmdArgs;
import cliclient.command.args.StartVocabularyTrainingSessionCmdArgs;
import cliclient.command.args.VocabularyTrainingSessionMode;
import cliclient.util.ColorCodes;
import cliclient.util.ThreadUtil;
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
public class StartVocabularyTrainingSessionCommandHandler implements CommandHandler {

    private static final int MIN_CORRECT_ANSWERS_COUNT = 0;

    private final VocabularyEntryApi vocabularyEntryApi;
    private final CommandLineAdapter adapter;
    private final VocabularyTrainingSessionStats stats;
    private final ThreadUtil threadUtil;

    @Override
    public void handle(CmdArgs cwa) {
        stats.reset();
        var args = (StartVocabularyTrainingSessionCmdArgs) cwa;
        executeTrainingSession(args);
    }

    @Override
    public Class<? extends CmdArgs> getCmdArgsClass() {
        return StartVocabularyTrainingSessionCmdArgs.class;
    }

    private void executeTrainingSession(StartVocabularyTrainingSessionCmdArgs args) {
        List<VocabularyEntryDto> entries = findEntries(args);
        if (entries.size() == 0) {
            adapter.error("No records");
        } else {
            adapter.writeLine("Loaded " + ColorCodes.cyan(entries.size()) + " entries.");
            executeTrainingSessionBasedOnMode(args.mode(), entries);
            stats.showAnswers();
            adapter.writeLine(ColorCodes.yellow("Training session finished!"));
        }
    }

    private List<VocabularyEntryDto> findEntries(StartVocabularyTrainingSessionCmdArgs args) {
        return switch (args.mode()) {
            case SYNONYMS -> vocabularyEntryApi.findWithSynonyms(args.entriesPerVocabularyTrainingSession());
            case TRANSLATIONS -> vocabularyEntryApi.findWithTranslations(args.entriesPerVocabularyTrainingSession());
            default -> throw new RuntimeException("Unsupported training session mode: " + args.mode());
        };
    }

    private void executeTrainingSessionBasedOnMode(VocabularyTrainingSessionMode mode, List<VocabularyEntryDto> entries) {
        var tracker = new EntryTracker(entries, mode);
        switch (mode) {
            case SYNONYMS -> executeSynonymsTrainingSession(tracker);
            case TRANSLATIONS -> executeTranslationsTrainingSession(tracker);
            case UNRECOGNIZED -> throw new RuntimeException("Unrecognized training session mode: " + mode);
        }
    }

    private void executeTranslationsTrainingSession(EntryTracker tracker) {
        executeTrainingSession(tracker, this::readTranslations, this::handleAnswerTranslations);
    }

    private String readSynonyms() {
        return readEquivalents("Synonyms");
    }

    private String readTranslations() {
        return readEquivalents("Translations");
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

//    private void executeAntonymsTrainingSession(EntryTracker tracker) {
//        executeTrainingSession(tracker, this::readAntonyms, this::handleAnswerAntonyms);
//    }

    private void executeTrainingSession(EntryTracker tracker,
                                        Supplier<String> answerSupplier,
                                        BiConsumer<Set<String>, VocabularyEntryDto> answerConsumer) {
        VocabularyEntryDto entry = tracker.showNextAndReturn();
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
                adapter.writeLine(ColorCodes.red("Max hints used"));
                stats.skipped(tracker.current);
                adapter.writeLine(tracker.current);
                threadUtil.sleepSeconds(1);
            } else {
                Set<String> parsedAnswer = parseEquivalents(answer);
                answerConsumer.accept(parsedAnswer, entry);
            }
            if (tracker.hasMoreEntries()) {
                entry = tracker.showNextAndReturn();
                answer = answerSupplier.get();
            }
        }
    }

    private void handleAnswerSynonyms(Set<String> synonymsAnswer, VocabularyEntryDto entry) {
        handleAnswer(synonymsAnswer, entry, entry::getSynonyms, this::processPartialSynonymsAnswer);
    }

    private void handleAnswerTranslations(Set<String> translationsAnswer, VocabularyEntryDto entry) {
        handleAnswer(translationsAnswer, entry, entry::getTranslations, this::processPartialTranslationsAnswer);
    }

//    private void handleAnswerAntonyms(Set<String> antonymsAnswer, VocabularyEntry entry) {
//        handleAnswer(antonymsAnswer, entry, entry::getAntonyms, this::processPartialAntonymsAnswer);
//    }

    private void handleAnswer(Set<String> answer,
                              VocabularyEntryDto entry,
                              Supplier<Set<String>> correctAnswer,
                              BiConsumer<Set<String>, VocabularyEntryDto> consumer) {
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

    private void processPartialSynonymsAnswer(Set<String> partialAnswer, VocabularyEntryDto entry) {
        processPartialAnswer(partialAnswer, entry, entry::getSynonyms, "synonyms");
    }

    private void processPartialTranslationsAnswer(Set<String> partialAnswer, VocabularyEntryDto entry) {
        processPartialAnswer(partialAnswer, entry, entry::getTranslations, "translations");
    }

//    private void processPartialAntonymsAnswer(Set<String> partialAnswer, VocabularyEntryDto entry) {
//        processPartialAnswer(partialAnswer, entry, entry::getAntonyms, "antonyms");
//    }

    private void processPartialAnswer(Set<String> partialAnswer,
                                      VocabularyEntryDto entry,
                                      Supplier<Set<String>> supplier,
                                      String label) {
        Set<String> originalEquivalentsCopy = new HashSet<>(supplier.get());
        originalEquivalentsCopy.removeAll(partialAnswer);
        incCorrectAnswersCount(entry);
        adapter.writeLine(ColorCodes.yellow("Correct."));
        adapter.writeLine("Other " + label + ": " + ColorCodes.yellow(String.join(", ", originalEquivalentsCopy)));
        stats.addPartialAnswer(entry);
    }

    private void processWrongAnswer(VocabularyEntryDto entry, Supplier<Set<String>> supplier) {
        decCorrectAnswersCount(entry);
        adapter.writeLine(ColorCodes.red("Wrong."));
        adapter.writeLine("Answer is: " + ColorCodes.red(String.join(", ", supplier.get())));
        stats.addWrongAnswer(entry);
    }

    private void processCorrectAnswer(VocabularyEntryDto entry) {
        incCorrectAnswersCount(entry);
        adapter.writeLine(ColorCodes.green("Correct!"));
        stats.addCorrectAnswer(entry);
    }

    private Set<String> parseEquivalents(String equivalents) {
        return Arrays.stream(equivalents.split(";"))
                .map(String::strip)
                .collect(toSet());
    }

    private void incCorrectAnswersCount(VocabularyEntryDto entry) {
        updateCorrectAnswersCount(entry, true);
    }

    private void decCorrectAnswersCount(VocabularyEntryDto entry) {
        updateCorrectAnswersCount(entry, false);
    }

    private void updateCorrectAnswersCount(VocabularyEntryDto entry, boolean correct) {
        int change = correct ? 1 : -1;
        int newValue = entry.getCorrectAnswersCount() + change;
        if (isValidCorrectAnswersCount(newValue)) {
            vocabularyEntryApi.patchEntry(PatchVocabularyEntryInput.builder()
                    .id(entry.getId())
                    .correctAnswersCount(newValue)
                    .build());
        }
    }

    private boolean isValidCorrectAnswersCount(int cacUpdated) {
        return MIN_CORRECT_ANSWERS_COUNT <= cacUpdated;
    }

    @RequiredArgsConstructor
    private class EntryTracker {
        final List<VocabularyEntryDto> entries;
        final VocabularyTrainingSessionMode mode;
        final int maxHintsPerEntry = 3;

        int index = 0;
        int hintsPerEntryUsed = 0;
        VocabularyEntryDto current;

        boolean shouldContinue(String answer) {
            return !"e".equalsIgnoreCase(answer) && hasMoreEntries();
        }

        boolean hasMoreEntries() {
            return index < entries.size();
        }

        VocabularyEntryDto showNextAndReturn() {
            current = entries.get(index);
            printCurrentWord(current);
            return current;
        }

        void printCurrentWord(VocabularyEntryDto entry) {
            adapter.writeLine("Word: " + ColorCodes.cyan(entry.getName()));
            adapter.newLine();
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
                case TRANSLATIONS -> current.getTranslations();
                default -> throw new RuntimeException("Unsupported tracker mode. Unable to get correct answers.");
            };
        }

        boolean allHintsExhausted() {
            return hintsPerEntryUsed > maxHintsPerEntry;
        }

    }

}
