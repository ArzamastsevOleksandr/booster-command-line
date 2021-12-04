package booster.load;

import booster.adapter.CommandLineAdapter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:import.properties")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ImportProgressTracker {

    private final CommandLineAdapter adapter;

    @Value("${enabled:false}")
    private boolean enabled;
    @Value("${progress.step:10}")
    private int progressStep;

    private int vocabularyEntriesImportCount;
    private int notesImportCount;

    void incVocabularyEntriesImportCount() {
        if (enabled) {
            ++vocabularyEntriesImportCount;

            if (vocabularyEntriesImportCount % progressStep == 0) {
                adapter.writeLine(vocabularyEntriesImportCount + " vocabulary entries imported");
            }
        }
    }

    void vocabularyEntriesImportFinished() {
        if (enabled) {
            adapter.writeLine("Vocabulary entries import process finished");
            adapter.writeLine("Total entries count: " + vocabularyEntriesImportCount);
        }
    }

    void incNotesImportCount() {
        if (enabled) {
            ++notesImportCount;

            if (notesImportCount % progressStep == 0) {
                adapter.writeLine(notesImportCount + " notes imported");
            }
        }
    }

    void notesImportFinished() {
        if (enabled) {
            adapter.writeLine("Notes import process finished");
            adapter.writeLine("Total notes count: " + notesImportCount);
        }
    }

}
