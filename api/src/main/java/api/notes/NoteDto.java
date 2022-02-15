package api.notes;

import api.tags.TagDto;
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
public class NoteDto {
    Long id;
    String content;
    Timestamp lastSeenAt;

    @Builder.Default
    Set<TagDto> tags = Set.of();
}
