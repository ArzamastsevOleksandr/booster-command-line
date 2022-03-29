package cliclient.command.args;

import cliclient.command.Command;

public record UploadCmdArgs(String filename) implements CmdArgs {

    @Override
    public Command getCommand() {
        return Command.UPLOAD;
    }

}
