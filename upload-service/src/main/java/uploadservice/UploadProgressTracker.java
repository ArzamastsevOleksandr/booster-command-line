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
class UploadProgressTracker {

    @Value("${upload.enabled:true}")
    boolean enabled;
    @Value("${upload.progress.step:10}")
    int progressStep;

    int vocabularyEntriesUploadCount;
    int notesUploadCount;

    void incVocabularyEntriesUploadCount() {
        if (enabled) {
            ++vocabularyEntriesUploadCount;

            if (vocabularyEntriesUploadCount % progressStep == 0) {
                log.info("{} vocabulary entries uploaded", vocabularyEntriesUploadCount);
            }
        }
    }

    void vocabularyEntriesUploadFinished() {
        if (enabled) {
            log.info("Vocabulary entries upload process finished");
            log.info("Total entries count: {}", vocabularyEntriesUploadCount);
        }
    }

    void incNotesUploadCount() {
        if (enabled) {
            ++notesUploadCount;

            if (notesUploadCount % progressStep == 0) {
                log.info("{} notes uploaded", notesUploadCount);
            }
        }
    }

    void notesUploadFinished() {
        if (enabled) {
            log.info("Notes upload process finished");
            log.info("Total notes count: {}", notesUploadCount);
        }
    }

}
