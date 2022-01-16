package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.model.VocabularyEntry;
import cliclient.service.ColorProcessor;
import cliclient.service.VocabularyEntryService;
import cliclient.util.ColorCodes;
import cliclient.util.ThreadUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Stateful component.
 * Ensure that the reset() method is called before usage.
 */
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TrainingSessionStats {

    private static final String SEPARATOR = "*************************************************";
    private static final int ENTRIES_PER_TRAINING_SESSION = 5;

    private final CommandLineAdapter adapter;
    private final ColorProcessor colorProcessor;
    private final VocabularyEntryService vocabularyEntryService;

    private final Set<VocabularyEntry> wrongAnswers = new HashSet<>();
    private final Set<VocabularyEntry> correctAnswers = new HashSet<>();
    private final Set<VocabularyEntry> partialAnswers = new HashSet<>();
    private final Set<VocabularyEntry> skipped = new HashSet<>();

    void reset() {
        wrongAnswers.clear();
        correctAnswers.clear();
        partialAnswers.clear();
        skipped.clear();
    }

    void displayAnswers() {
        displayCorrectAnswers();
        displayPartialAnswers();
        displayWrongAnswers();
        displaySkipped();
    }

    private void displayCorrectAnswers() {
        displayAnswers(correctAnswers, ColorCodes.green("Correct answers " + fraction(correctAnswers.size())));
    }

    private void displayPartialAnswers() {
        displayAnswers(partialAnswers, ColorCodes.yellow("Partial answers " + fraction(partialAnswers.size())));
    }

    private void displayWrongAnswers() {
        displayAnswers(wrongAnswers, ColorCodes.red("Wrong answers " + fraction(wrongAnswers.size())));
    }

    private void displaySkipped() {
        displayAnswers(skipped, ColorCodes.blue("Skipped " + fraction(skipped.size())));
    }

    private void displayAnswers(Set<VocabularyEntry> answers, String label) {
        if (!answers.isEmpty()) {
            ThreadUtil.sleepSeconds(1);
            adapter.writeLine(purpleSeparator());
            adapter.writeLine(label);
            adapter.newLine();
            answers.stream()
                    .map(VocabularyEntry::getId)
                    .map(vocabularyEntryService::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(entry -> {
                        adapter.writeLine(colorProcessor.coloredEntry(entry));
                        vocabularyEntryService.updateLastSeenAtById(entry.getId());
                    });
            adapter.newLine();
        }
    }

    private String purpleSeparator() {
        return ColorCodes.purple(SEPARATOR);
    }

    private String fraction(int numerator) {
        return "(" + numerator + "/" + ENTRIES_PER_TRAINING_SESSION + "):";
    }

    void addCorrectAnswer(VocabularyEntry entry) {
        correctAnswers.add(entry);
    }

    void addWrongAnswer(VocabularyEntry entry) {
        wrongAnswers.add(entry);
    }

    void addPartialAnswer(VocabularyEntry entry) {
        partialAnswers.add(entry);
    }

    public void skipped(VocabularyEntry entry) {
        skipped.add(entry);
    }

}
