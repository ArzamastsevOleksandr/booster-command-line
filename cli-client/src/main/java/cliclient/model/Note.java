package cliclient.model;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Deprecated
@Value
@Builder(toBuilder = true)
public class Note {

    long id;
    String content;
    @Builder.Default
    Set<String> tags = Set.of();

}
