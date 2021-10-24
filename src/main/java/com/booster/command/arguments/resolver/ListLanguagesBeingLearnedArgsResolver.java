package com.booster.command.arguments.resolver;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.ListLanguagesBeingLearnedArgs;
import com.booster.dao.LanguageBeingLearnedDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.booster.command.Command.LIST_LANGUAGES_BEING_LEARNED;

@Component
@RequiredArgsConstructor
public class ListLanguagesBeingLearnedArgsResolver implements ArgsResolver {

    private static final String ID_FLAG = "id";

    private final LanguageBeingLearnedDao languageBeingLearnedDao;

    @Override
    public CommandWithArguments resolve(List<String> args) {
        var builder = getCommandBuilder();
        try {
            Map<String, String> flag2value = checkFlagsWithValuesAndReturn(args);
            if (flag2value.containsKey(ID_FLAG)) {
                checkIfIdIsCorrectNumber(flag2value.get(ID_FLAG));
                checkIfLanguageBeingLearnedExistsWithId(Long.parseLong(flag2value.get(ID_FLAG)));
                return builder
                        .args(ListLanguagesBeingLearnedArgs.of(Long.parseLong(flag2value.get(ID_FLAG))))
                        .build();
            } else {
                return builder
                        .args(ListLanguagesBeingLearnedArgs.empty())
                        .build();
            }
        } catch (ArgsValidationException e) {
            return builder
                    .argErrors(e.getArgErrors())
                    .build();
        }
    }

    // todo: an aggregate validator component with all possible validation methods?
    private void checkIfLanguageBeingLearnedExistsWithId(long id) {
        if (!languageBeingLearnedDao.existsWithId(id)) {
            throw new ArgsValidationException(List.of("Language being learned does not exist with id: " + id));
        }
    }

    @Override
    public Command command() {
        return LIST_LANGUAGES_BEING_LEARNED;
    }

}
