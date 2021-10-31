package com.booster.parser;

import com.booster.command.arguments.CommandWithArguments;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandLineInputTransformer {

    private final CommandLineInputTokenizer tokenizer;
    private final TokenSequenceValidator validator;
    private final TokenSequenceTransformer transformer;

    public CommandWithArguments fromString(String input) {
        List<Token> tokens = tokenizer.parseIntoTokens(input);
        TokenValidationResult validationResult = validator.validate(tokens);
        if (validationResult.hasNoErrors()) {
            return transformer.transform(tokens);
        }
        return CommandWithArguments.withErrors(validationResult.getErrors());
    }

}
