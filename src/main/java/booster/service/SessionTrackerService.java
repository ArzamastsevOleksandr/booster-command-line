package booster.service;

import booster.dao.params.AddCause;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Optional;

// todo: persist statistics to gather aggregated reports
@Service
@Getter
public class SessionTrackerService {

    // todo: exact entries, notes, tags, training session details etc
    private int vocabularyEntriesAddedCount;
    private int notesAddedCount;

    private int vocabularyEntriesImportedCount;
    private int notesImportedCount;

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

        String toString = builder.toString();
        return toString.isBlank() ? Optional.empty() : Optional.of("Session statistics:\n" + toString);
    }

}
