package uploadservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
class ImportProgressTracker {

    @Value("${import.enabled:true}")
    boolean enabled;
    @Value("${import.progress.step:10}")
    int progressStep;

    int vocabularyEntriesImportCount;
    int notesImportCount;

    void incVocabularyEntriesImportCount() {
        if (enabled) {
            ++vocabularyEntriesImportCount;

            if (vocabularyEntriesImportCount % progressStep == 0) {
                log.info("{} vocabulary entries imported", vocabularyEntriesImportCount);
            }
        }
    }

    void vocabularyEntriesImportFinished() {
        if (enabled) {
            log.info("Vocabulary entries import process finished");
            log.info("Total entries count: {}", vocabularyEntriesImportCount);
        }
    }

    void incNotesImportCount() {
        if (enabled) {
            ++notesImportCount;

            if (notesImportCount % progressStep == 0) {
                log.info("{} notes imported", notesImportCount);
            }
        }
    }

    void notesImportFinished() {
        if (enabled) {
            log.info("Notes import process finished");
            log.info("Total notes count: {}", notesImportCount);
        }
    }

}
