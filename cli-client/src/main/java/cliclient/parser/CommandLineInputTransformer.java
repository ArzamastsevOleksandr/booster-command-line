package cliclient.parser;

import cliclient.command.arguments.CommandWithArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandLineInputTransformer {

    private final Tokenizer tokenizer;
    private final TokenValidator tokenValidator;
    private final TokenSequenceTransformer tokenSequenceTransformer; // todo: a better name

    public CommandWithArgs toCommandWithArgs(String input) {
        List<Token> tokens = tokenizer.parseIntoTokens(input);
        TokenValidationResult tokenValidationResult = tokenValidator.validate(tokens);
        return tokenSequenceTransformer.transform(tokenValidationResult);
    }

}
