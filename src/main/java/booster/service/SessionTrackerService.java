package booster.service;

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

    public void incVocabularyEntriesAddedCount() {
        vocabularyEntriesAddedCount++;
    }

    public void incNotesAddedCount() {
        notesAddedCount++;
    }

    public Optional<String> getStatistics() {
        var builder = new StringBuilder();
        if (vocabularyEntriesAddedCount != 0) {
            builder.append("New vocabulary entries added: ").append(vocabularyEntriesAddedCount).append("\n");
        }
        if (notesAddedCount != 0) {
            builder.append("New notes added: ").append(notesAddedCount).append("\n");
        }
        String toString = builder.toString();
        return toString.isBlank() ? Optional.empty() : Optional.of("Session statistics:\n" + toString);
    }

}
