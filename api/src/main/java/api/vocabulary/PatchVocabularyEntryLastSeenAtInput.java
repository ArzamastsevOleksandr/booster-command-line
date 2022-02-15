package api.vocabulary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchVocabularyEntryLastSeenAtInput {
    List<Long> ids;

    @Builder.Default
    Timestamp lastSeenAt = new Timestamp(System.currentTimeMillis());
}
