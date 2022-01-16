package cliclient.command.arguments.validator;

import java.util.List;
import java.util.Objects;

// todo: hide inside ArgValidator
public class ArgsValidationException extends RuntimeException {

    public final List<String> errors;

    public ArgsValidationException(String... errors) {
        this.errors = List.of(Objects.requireNonNull(errors, "errors can not be null"));
    }

}
