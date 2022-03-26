package cliclient.command.service;

import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.EmptyCommandArgs;

public record CommandArgsResult(Command command, CommandArgs commandArgs, String error) {

    static CommandArgsResult success(CommandArgs commandArgs) {
        return new CommandArgsResult(null, commandArgs, null);
    }

    static CommandArgsResult withErrors(String error, Command command) {
        return new CommandArgsResult(command, null, error);
    }

    static CommandArgsResult empty() {
        return new CommandArgsResult(null, new EmptyCommandArgs(), null);
    }

    boolean hasErrors() {
        return error != null;
    }
    
}
