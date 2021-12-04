package booster.command.handler;

import booster.adapter.CommandLineAdapter;
import booster.model.VocabularyEntry;
import booster.service.ColorProcessor;
import booster.util.ColorCodes;
import booster.util.ThreadUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
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

    private final Set<VocabularyEntry> wrongAnswers = new HashSet<>();
    private final Set<VocabularyEntry> correctAnswers = new HashSet<>();
    private final Set<VocabularyEntry> partialAnswers = new HashSet<>();

    void reset() {
        wrongAnswers.clear();
        correctAnswers.clear();
        partialAnswers.clear();
    }

    void displayAnswers() {
        displayCorrectAnswers();
        displayPartialAnswers();
        displayWrongAnswers();
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

    private void displayAnswers(Set<VocabularyEntry> answers, String label) {
        if (!answers.isEmpty()) {
            ThreadUtil.sleepSeconds(1);
            adapter.writeLine(purpleSeparator());
            adapter.writeLine(label);
            adapter.newLine();
            answers.stream().map(colorProcessor::coloredEntry).forEach(adapter::writeLine);
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

}
