package cliclient.exception;

import cliclient.command.Command;
import lombok.Getter;

public class IncorrectCommandFormatException extends RuntimeException {

    @Getter
    final Command command;

    public IncorrectCommandFormatException(String message, Command command) {
        super(message);
        this.command = command;
    }

}
