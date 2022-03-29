package cliclient.command.args;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListVocabularyEntriesCmdWithArgs implements CmdArgs {

    Long id;
    Integer pagination;
    String substring;

    public Optional<Long> id() {
        return Optional.ofNullable(id);
    }

    public Optional<String> substring() {
        return Optional.ofNullable(substring);
    }

}
