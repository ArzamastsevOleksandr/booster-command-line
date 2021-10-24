package com.booster.command.arguments.resolver;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.DeleteVocabularyArgs;
import com.booster.dao.VocabularyDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.booster.command.Command.DELETE_VOCABULARY;

@Component
@RequiredArgsConstructor
public class DeleteVocabularyArgsResolver implements ArgsResolver {

    private static final String ID_FLAG = "id";

    private final VocabularyDao vocabularyDao;

    @Override
    public CommandWithArguments resolve(List<String> args) {
        CommandWithArguments.CommandWithArgumentsBuilder builder = getCommandBuilder();
        try {
            checkIfArgumentsAreSpecified(args);

            Map<String, String> flag2value = checkFlagsWithValuesAndReturn(args);

            checkIfMandatoryFlagsArePresent(flag2value, Set.of(ID_FLAG));
            checkIfIdIsCorrectNumber(flag2value.get(ID_FLAG));
            checkIfVocabularyExistsWithId(Long.parseLong(flag2value.get(ID_FLAG)));

            return builder
                    .args(new DeleteVocabularyArgs(Long.parseLong(flag2value.get(ID_FLAG))))
                    .build();
        } catch (ArgsValidationException e) {
            return builder
                    .argErrors(e.getArgErrors())
                    .build();
        }
    }

    @Override
    public Command command() {
        return DELETE_VOCABULARY;
    }

    private void checkIfVocabularyExistsWithId(long id) {
        if (!vocabularyDao.existsWithId(id)) {
            throw new ArgsValidationException(List.of("Vocabulary with id: " + id + " does not exist."));
        }
    }

}
