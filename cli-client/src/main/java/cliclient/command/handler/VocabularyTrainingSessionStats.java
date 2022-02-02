package cliclient.command.handler;

import api.vocabulary.VocabularyEntryDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.feign.vocabulary.VocabularyEntryControllerApiClient;
import cliclient.service.VocabularyEntryService;
import cliclient.util.ColorCodes;
import cliclient.util.ThreadUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Stateful component.
 * Ensure that the reset() method is called before usage.
 */
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class VocabularyTrainingSessionStats {

    private static final String SEPARATOR = "*************************************************";

    private final CommandLineAdapter adapter;
    private final VocabularyEntryService vocabularyEntryService;
    private final VocabularyEntryControllerApiClient vocabularyEntryControllerApiClient;

    private final Set<VocabularyEntryDto> wrongAnswers = new HashSet<>();
    private final Set<VocabularyEntryDto> correctAnswers = new HashSet<>();
    private final Set<VocabularyEntryDto> partialAnswers = new HashSet<>();
    private final Set<VocabularyEntryDto> skipped = new HashSet<>();

    @Value("${session.vocabulary.size:10}")
    private int entriesPerSession;

    void reset() {
        wrongAnswers.clear();
        correctAnswers.clear();
        partialAnswers.clear();
        skipped.clear();
    }

    void showAnswers() {
        showCorrectAnswers();
        showPartialAnswers();
        showWrongAnswers();
        showSkipped();
    }

    private void showCorrectAnswers() {
        showAnswers(correctAnswers, ColorCodes.green("Correct answers " + fraction(correctAnswers.size())));
    }

    private void showPartialAnswers() {
        showAnswers(partialAnswers, ColorCodes.yellow("Partial answers " + fraction(partialAnswers.size())));
    }

    private void showWrongAnswers() {
        showAnswers(wrongAnswers, ColorCodes.red("Wrong answers " + fraction(wrongAnswers.size())));
    }

    private void showSkipped() {
        showAnswers(skipped, ColorCodes.blue("Skipped " + fraction(skipped.size())));
    }

    private void showAnswers(Set<VocabularyEntryDto> answers, String label) {
        if (!answers.isEmpty()) {
            ThreadUtil.sleepSeconds(1);
            adapter.writeLine(purpleSeparator());
            adapter.writeLine(label);
            adapter.newLine();
            answers.stream()
                    .map(VocabularyEntryDto::getId)
                    .map(vocabularyEntryControllerApiClient::findById)
                    .forEach(entry -> {
                        adapter.writeLine(entry);
                        vocabularyEntryService.updateLastSeenAt(entry);
                    });
            adapter.newLine();
        }
    }

    private String purpleSeparator() {
        return ColorCodes.purple(SEPARATOR);
    }

    private String fraction(int numerator) {
        return "(" + numerator + "/" + entriesPerSession + "):";
    }

    void addCorrectAnswer(VocabularyEntryDto entry) {
        correctAnswers.add(entry);
    }

    void addWrongAnswer(VocabularyEntryDto entry) {
        wrongAnswers.add(entry);
    }

    void addPartialAnswer(VocabularyEntryDto entry) {
        partialAnswers.add(entry);
    }

    public void skipped(VocabularyEntryDto entry) {
        skipped.add(entry);
    }

}
