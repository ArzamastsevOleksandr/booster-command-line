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
public class NoteDto {
    Long id;
    String content;
    Timestamp lastSeenAt;
    // todo: add tags
}
