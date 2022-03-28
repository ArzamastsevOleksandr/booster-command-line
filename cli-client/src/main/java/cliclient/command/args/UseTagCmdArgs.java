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
public class UseTagCmdArgs implements CmdArgs {

    Long noteId;
    String tag;

    @Override
    public Command getCommand() {
        return Command.USE_TAG;
    }

}
