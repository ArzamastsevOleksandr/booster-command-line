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
public class ErroneousCmdArgs implements CmdArgs {

    Command command;
    String error;

    @Override
    public Command getCommand() {
        return command;
    }

    @Override
    public boolean hasNoErrors() {
        return false;
    }
}
