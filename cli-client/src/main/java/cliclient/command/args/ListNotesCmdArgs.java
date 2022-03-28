package cliclient.command.args;

import cliclient.command.Command;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListNotesCmdArgs implements CmdArgs {

    Long id;
    Integer pagination;

    public Optional<Long> id() {
        return Optional.ofNullable(id);
    }

    @Override
    public Command getCommand() {
        return Command.LIST_NOTES;
    }

}
