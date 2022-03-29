package cliclient.command.args;

import cliclient.command.Command;

public record DownloadCmdArgs(String filename) implements CmdArgs {

    @Override
    public Command getCommand() {
        return Command.DOWNLOAD;
    }

}
