package cliclient.dao.params;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class AddNoteDaoParams {

    String content;

    @Builder.Default
    Set<String> tags = Set.of();

    @Builder.Default
    AddCause addCause = AddCause.CREATE;

}
