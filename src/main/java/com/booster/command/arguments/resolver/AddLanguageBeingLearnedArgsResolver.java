package com.booster.command.arguments.resolver;

import com.booster.command.arguments.AddLanguageBeingLearnedArgs;
import com.booster.command.arguments.CommandWithArguments;
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

    public CommandWithArguments resolve(List<String> args) {
        var builder = getBuilder();
        try {
            checkIfArgumentsAreSpecified(args);

            Map<String, String> flag2value = checkFlagsWithValuesAndReturn(args);
            checkIfMandatoryFlagsArePresent(flag2value, Set.of(ID_FLAG));
            checkIfIdIsCorrectNumber(flag2value.get(ID_FLAG));
            checkIfLanguageExistsWithId(Long.parseLong(flag2value.get(ID_FLAG)));
            // todo: SQL [insert into language_being_learned (language_id) values (?)]; ERROR: duplicate key value violates unique constraint "language_being_learned__language_id__index"

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
    public String commandString() {
        return ADD_LANGUAGE_BEING_LEARNED.extendedToString();
    }

    private CommandWithArguments.CommandWithArgumentsBuilder getBuilder() {
        return CommandWithArguments.builder()
                .command(ADD_LANGUAGE_BEING_LEARNED);
    }

    private void checkIfLanguageExistsWithId(long languageId) {
        if (!languageDao.existsWithId(languageId)) {
            throw new ArgsValidationException(List.of("Language with id: " + languageId + " does not exist."));
        }
    }

}
