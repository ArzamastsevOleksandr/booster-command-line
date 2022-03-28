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
public class SingleCmdArgs implements CmdArgs {

    Command command;

    @Override
    public Command getCommand() {
        return command;
    }

}
