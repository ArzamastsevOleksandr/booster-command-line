package com.booster.command.arguments.resolver;

import com.booster.command.arguments.AddVocabularyArgs;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.dao.VocabularyDao;
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

    private final LanguageBeingLearnedDao languageBeingLearnedDao;
    private final VocabularyDao vocabularyDao;

    public CommandWithArguments resolve(List<String> args) {
        CommandWithArguments.CommandWithArgumentsBuilder builder = getBuilder();
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
    public String commandString() {
        return ADD_VOCABULARY.extendedToString();
    }

    private CommandWithArguments.CommandWithArgumentsBuilder getBuilder() {
        return CommandWithArguments.builder()
                .command(ADD_VOCABULARY);
    }

    private void checkIfLanguageBeingLearnedExistsWithId(long id) {
        if (!languageBeingLearnedDao.existsWithId(id)) {
            throw new ArgsValidationException(List.of("Language being learned with id: " + id + " does not exist."));
        }
    }

    private void checkIfVocabularyNameIsAlreadyInUse(Map<String, String> flag2value) {
        long id = Long.parseLong(flag2value.get(ID_FLAG));
        String name = flag2value.get(NAME_FLAG);
        if (vocabularyDao.existsWithNameForLanguageBeingLearned(name, id)) {
            var argErrors = List.of(
                    "Vocabulary with name: " + name + " already exists for the language being learned with id: " + id + "."
            );
            throw new ArgsValidationException(argErrors);
        }
    }

}
