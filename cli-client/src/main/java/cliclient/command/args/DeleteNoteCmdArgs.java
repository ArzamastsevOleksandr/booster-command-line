package cliclient.command.args;

import cliclient.command.Command;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteNoteCmdArgs implements CmdArgs {

    Long id;

    @Override
    public Command getCommand() {
        return Command.DELETE_NOTE;
    }

}
