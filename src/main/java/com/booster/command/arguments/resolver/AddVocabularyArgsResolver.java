package com.booster.command.arguments.resolver;

import com.booster.command.Command;
import com.booster.command.arguments.AddVocabularyArgs;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.service.LanguageBeingLearnedService;
import com.booster.service.VocabularyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.booster.command.Command.ADD_VOCABULARY;

@Component
@RequiredArgsConstructor
public class AddVocabularyArgsResolver implements ArgsResolver {

    private static final String NAME_FLAG = "n";
    private static final String ID_FLAG = "id";

    private final LanguageBeingLearnedService languageBeingLearnedService;
    private final VocabularyService vocabularyService;

    @Override
    public CommandWithArguments resolve(List<String> args) {
        CommandWithArguments.CommandWithArgumentsBuilder builder = getCommandBuilder();
        try {
            checkIfArgumentsAreSpecified(args);

            Map<String, String> flag2value = checkFlagsWithValuesAndReturn(args);

            checkIfMandatoryFlagsArePresent(flag2value, Set.of(NAME_FLAG, ID_FLAG));
            checkIfIdIsCorrectNumber(flag2value.get(ID_FLAG));
            checkIfLanguageBeingLearnedExistsWithId(Long.parseLong(flag2value.get(ID_FLAG)));
            checkIfVocabularyNameIsAlreadyInUse(flag2value);

            return builder
                    .args(new AddVocabularyArgs(Long.parseLong(flag2value.get(ID_FLAG)), flag2value.get(NAME_FLAG)))
                    .build();
        } catch (ArgsValidationException e) {
            return builder
                    .argErrors(e.getArgErrors())
                    .build();
        }
    }

    @Override
    public Command command() {
        return ADD_VOCABULARY;
    }

    private void checkIfLanguageBeingLearnedExistsWithId(long id) {
        if (!languageBeingLearnedService.existsWithId(id)) {
            throw new ArgsValidationException(List.of("Language being learned with id: " + id + " does not exist."));
        }
    }

    private void checkIfVocabularyNameIsAlreadyInUse(Map<String, String> flag2value) {
        long id = Long.parseLong(flag2value.get(ID_FLAG));
        String name = flag2value.get(NAME_FLAG);
        if (vocabularyService.existsWithNameForLanguageBeingLearnedId(name, id)) {
            var argErrors = List.of(
                    "Vocabulary with name: " + name + " already exists for the language being learned with id: " + id + "."
            );
            throw new ArgsValidationException(argErrors);
        }
    }

}
