package cliclient.service;

import cliclient.dao.params.AddCause;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.Optional;

// todo: persist statistics to gather aggregated reports
@Service
@Getter
public class SessionTrackerService {

    private long initStartTime;
    // todo: exact entries, notes, tags, training session details etc
    private int vocabularyEntriesAddedCount;
    private int notesAddedCount;

    private int vocabularyEntriesImportedCount;
    private int notesImportedCount;

    @PostConstruct
    void postConstruct() {
        initStartTime = System.currentTimeMillis();
    }

    public void incVocabularyEntriesCount(AddCause addCause) {
        if (addCause == AddCause.IMPORT) {
            vocabularyEntriesImportedCount++;
        } else {
            vocabularyEntriesAddedCount++;
        }
    }

    public void incNotesCount(AddCause addCause) {
        if (addCause == AddCause.IMPORT) {
            notesImportedCount++;
        } else {
            notesAddedCount++;
        }
    }

    public Optional<String> getStatistics() {
        var builder = new StringBuilder();

        if (vocabularyEntriesAddedCount != 0) {
            builder.append("New vocabulary entries added: ").append(vocabularyEntriesAddedCount).append("\n");
        }
        if (notesAddedCount != 0) {
            builder.append("New notes added: ").append(notesAddedCount).append("\n");
        }

        if (vocabularyEntriesImportedCount != 0) {
            builder.append("Vocabulary entries imported: ").append(vocabularyEntriesImportedCount).append("\n");
        }
        if (notesImportedCount != 0) {
            builder.append("Notes imported: ").append(notesImportedCount).append("\n");
        }

        long millisSpent = System.currentTimeMillis() - initStartTime;

        builder.append("Time spent: ").append(convertToExpressiveFormat(millisSpent)).append('\n');
        return Optional.of("Session statistics:\n" + builder);
    }

    private String convertToExpressiveFormat(long millisSpent) {
        LocalTime localTime = LocalTime.ofSecondOfDay(millisSpent / 1000);
        var builder = new StringBuilder();
        if (localTime.getHour() > 0) {
            builder.append(localTime.getHour()).append(" h ");
        }
        if (localTime.getMinute() > 0) {
            builder.append(localTime.getMinute()).append(" m ");
        }
        builder.append(localTime.getSecond()).append(" s ").append('\n');
        return builder.toString();
    }

}
