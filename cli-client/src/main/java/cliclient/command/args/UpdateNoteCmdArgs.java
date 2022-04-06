package cliclient.command.args;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNoteCmdArgs implements CmdArgs {
    Long id;
    String content;
    Set<String> removeTags = Set.of();
    Set<String> addTags = Set.of();
}
