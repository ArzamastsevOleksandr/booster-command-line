package com.booster.command.arguments.resolver;

import com.booster.command.Command;
import com.booster.command.arguments.AddLanguageBeingLearnedArgs;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.dao.LanguageDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.booster.command.Command.ADD_LANGUAGE_BEING_LEARNED;

@Component
@RequiredArgsConstructor
public class AddLanguageBeingLearnedArgsResolver implements ArgsResolver {

    private static final String ID_FLAG = "id";

    private final LanguageDao languageDao;
    private final LanguageBeingLearnedDao languageBeingLearnedDao;

    @Override
    public CommandWithArguments resolve(List<String> args) {
        var builder = getCommandBuilder();
        try {
            checkIfArgumentsAreSpecified(args);

            Map<String, String> flag2value = checkFlagsWithValuesAndReturn(args);
            checkIfMandatoryFlagsArePresent(flag2value, Set.of(ID_FLAG));
            checkIfIdIsCorrectNumber(flag2value.get(ID_FLAG));
            checkIfLanguageExistsWithId(Long.parseLong(flag2value.get(ID_FLAG)));
            checkIfLanguageBeingLearnedAlreadyExists(Long.parseLong(flag2value.get(ID_FLAG)));

            return builder
                    .args(new AddLanguageBeingLearnedArgs(Long.parseLong(flag2value.get(ID_FLAG))))
                    .build();
        } catch (ArgsValidationException e) {
            return builder
                    .argErrors(e.getArgErrors())
                    .build();
        }
    }

    @Override
    public Command command() {
        return ADD_LANGUAGE_BEING_LEARNED;
    }

    private void checkIfLanguageExistsWithId(long languageId) {
        if (!languageDao.existsWithId(languageId)) {
            throw new ArgsValidationException(List.of("Language with id: " + languageId + " does not exist."));
        }
    }

    private void checkIfLanguageBeingLearnedAlreadyExists(long id) {
        if (languageBeingLearnedDao.existsWithLanguageId(id)) {
            throw new ArgsValidationException(List.of("Language being learned already exists for language id: " + id));
        }
    }

}
