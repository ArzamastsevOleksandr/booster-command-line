package cliclient.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalTime;

// todo: persist statistics to gather aggregated reports
@Service
public class SessionTrackerService {

    private long initStartTime;
    // todo: exact entries, notes, tags, training session details etc
    public int vocabularyEntriesAddedCount;
    public int notesAddedCount;

    public int vocabularyEntriesImportedCount;
    public int notesImportedCount;

    @PostConstruct
    void postConstruct() {
        initStartTime = System.currentTimeMillis();
    }

    public String getStatistics() {
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
        return "Session statistics:\n" + builder;
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
