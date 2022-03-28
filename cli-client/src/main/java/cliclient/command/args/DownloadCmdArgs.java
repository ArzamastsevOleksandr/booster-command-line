package cliclient.command.args;

import cliclient.command.Command;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadCmdArgs implements CmdArgs {

    String filename;

    @Override
    public Command getCommand() {
        return Command.DOWNLOAD;
    }

}
