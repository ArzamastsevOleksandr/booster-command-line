package api.notes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNoteInput {
    Long id;
    String content;

    @Builder.Default
    Set<String> tags = Set.of();
}
