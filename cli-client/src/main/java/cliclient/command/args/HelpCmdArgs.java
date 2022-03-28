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
public class HelpCmdArgs implements CmdArgs {

    public Command helpTarget;

    public Optional<Command> getHelpTarget() {
        return Optional.ofNullable(helpTarget);
    }

    public Command getCommand() {
        return Command.HELP;
    }

}
