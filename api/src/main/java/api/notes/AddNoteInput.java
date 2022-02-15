package api.notes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddNoteInput {
    String content;

    @Builder.Default
    Timestamp lastSeenAt = new Timestamp(System.currentTimeMillis());

    @Builder.Default
    Set<String> tags = Set.of();
}
