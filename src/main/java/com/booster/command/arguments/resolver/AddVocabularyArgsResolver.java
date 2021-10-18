package com.booster.command.arguments.resolver;

import com.booster.command.arguments.AddVocabularyArgs;
import com.booster.command.arguments.Args;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.dao.VocabularyDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.booster.command.Command.ADD_VOCABULARY;

@Component
@RequiredArgsConstructor
public class AddVocabularyArgsResolver {

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

    private CommandWithArguments.CommandWithArgumentsBuilder getBuilder() {
        return CommandWithArguments.builder()
                .command(ADD_VOCABULARY);
    }

    private void checkIfArgumentsAreSpecified(List<String> args) {
        if (args.size() == 0) {
            throw new ArgsValidationException(List.of("No args specified for the " + commandString() + " command."));
        }
    }

    private Map<String, String> checkFlagsWithValuesAndReturn(List<String> args) {
        try {
            return args.stream()
                    .map(Args::splitAndStrip)
                    .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
        } catch (ArrayIndexOutOfBoundsException e) {
            List<String> argErrors = List.of(
                    "Flag must come together with the value, separated by the '=' sign.",
                    "All flag-value pairs must be separated with a space."
            );
            throw new ArgsValidationException(argErrors);
        }
    }

    private void checkIfMandatoryFlagsArePresent(Map<String, String> flag2value, Set<String> mandatoryFlags) {
        var providedFlags = new HashSet<>(flag2value.keySet());
        var mandatoryFlagsCopy = new HashSet<>(mandatoryFlags);
        mandatoryFlagsCopy.removeAll(providedFlags);
        if (!mandatoryFlagsCopy.isEmpty()) {
            var argErrors = List.of(
                    "Mandatory flags (" + String.join(",", mandatoryFlagsCopy) + ") are missing"
            );
            throw new ArgsValidationException(argErrors);
        }
    }

    private void checkIfIdIsCorrectNumber(String idValue) {
        if (isNotLongType(idValue)) {
            throw new ArgsValidationException(List.of("Id must be a positive integer number. Got: " + idValue + "."));
        }
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

    private String commandString() {
        return ADD_VOCABULARY.extendedToString();
    }

    private boolean isNotLongType(String s) {
        try {
            Long.parseLong(s);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

}
