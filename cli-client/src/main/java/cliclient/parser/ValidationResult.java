package cliclient.parser;

import cliclient.command.Command;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
class ValidationResult {

    final CommandValidationResult commandValidationResult;
    final Command command;
    final String comment;

    ValidationResult(CommandValidationResult commandValidationResult) {
        this(commandValidationResult, null, null);
    }

    ValidationResult(CommandValidationResult commandValidationResult, String comment) {
        this(commandValidationResult, null, comment);
    }

    ValidationResult(CommandValidationResult commandValidationResult, Command command) {
        this(commandValidationResult, command, null);
    }

}
