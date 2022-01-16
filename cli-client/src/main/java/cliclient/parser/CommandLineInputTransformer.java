package cliclient.parser;

import cliclient.command.arguments.CommandWithArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

// todo: clear naming
@Component
@RequiredArgsConstructor
public class CommandLineInputTransformer {

    private final CommandLineInputTokenizer tokenizer;
    private final TokenSequenceValidator validator;
    private final TokenSequenceTransformer transformer;

    public CommandWithArgs fromString(String input) {
        List<Token> tokens = tokenizer.parseIntoTokens(input);
        TokenValidationResult validationResult = validator.validate(tokens);
        if (validationResult.hasNoErrors()) {
            return transformer.transform(tokens);
        }
        return CommandWithArgs.withErrors(validationResult.getErrors());
    }

}
