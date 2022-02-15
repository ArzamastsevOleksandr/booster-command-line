package api.notes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddNoteInput {
    String content;

    @Builder.Default
    Timestamp lastSeenAt = new Timestamp(System.currentTimeMillis());
}
