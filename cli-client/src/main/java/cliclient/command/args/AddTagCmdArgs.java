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
public class AddTagCmdArgs implements CmdArgs {

    String name;

    @Override
    public Command getCommand() {
        return Command.ADD_TAG;
    }

}
