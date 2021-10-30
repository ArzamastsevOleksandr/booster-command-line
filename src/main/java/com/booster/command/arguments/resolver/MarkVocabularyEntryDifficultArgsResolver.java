package com.booster.command.arguments.resolver;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.MarkVocabularyEntryDifficultArgs;
import com.booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.booster.command.Command.MARK_VOCABULARY_ENTRY_DIFFICULT;

@Component
@RequiredArgsConstructor
public class MarkVocabularyEntryDifficultArgsResolver implements ArgsResolver {

    private static final String ID_FLAG = "id";

    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public CommandWithArguments resolve(List<String> args) {
        CommandWithArguments.CommandWithArgumentsBuilder builder = getCommandBuilder();
        try {
            checkIfArgumentsAreSpecified(args);

            Map<String, String> flag2value = checkFlagsWithValuesAndReturn(args);

            checkIfMandatoryFlagsArePresent(flag2value, Set.of(ID_FLAG));
            checkIfIdIsCorrectNumber(flag2value.get(ID_FLAG));
            checkIfVocabularyEntryExistsWithId(Long.parseLong(flag2value.get(ID_FLAG)));

            return builder.args(new MarkVocabularyEntryDifficultArgs(Long.parseLong(flag2value.get(ID_FLAG))))
                    .build();
        } catch (ArgsValidationException e) {
            return builder
                    .argErrors(e.getArgErrors())
                    .build();
        }
    }

    private void checkIfVocabularyEntryExistsWithId(long id) {
        if (!vocabularyEntryService.existsWithId(id)) {
            throw new ArgsValidationException(List.of("Vocabulary entry does not exist with id: " + id));
        }
    }

    @Override
    public Command command() {
        return MARK_VOCABULARY_ENTRY_DIFFICULT;
    }

}
