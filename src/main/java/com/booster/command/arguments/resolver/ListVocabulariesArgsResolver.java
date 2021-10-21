package com.booster.command.arguments.resolver;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.ListVocabulariesArgs;
import com.booster.dao.VocabularyDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.booster.command.Command.LIST_VOCABULARIES;

@Component
@RequiredArgsConstructor
public class ListVocabulariesArgsResolver implements ArgsResolver {

    private static final String ID_FLAG = "id";

    private final VocabularyDao vocabularyDao;

    public CommandWithArguments resolve(List<String> args) {
        var builder = getCommandBuilder();
        try {
            Map<String, String> flag2value = checkFlagsWithValuesAndReturn(args);
            if (flag2value.containsKey(ID_FLAG)) {
                checkIfIdIsCorrectNumber(flag2value.get(ID_FLAG));
                checkIfVocabularyExistsWithId(Long.parseLong(flag2value.get(ID_FLAG)));
                return builder
                        .args(ListVocabulariesArgs.of(Long.parseLong(flag2value.get(ID_FLAG))))
                        .build();
            } else {
                return builder
                        .args(ListVocabulariesArgs.empty())
                        .build();
            }
        } catch (ArgsValidationException e) {
            return builder
                    .argErrors(e.getArgErrors())
                    .build();
        }
    }

    private void checkIfVocabularyExistsWithId(long id) {
        if (!vocabularyDao.existsWithId(id)) {
            throw new ArgsValidationException(List.of("Vocabulary does not exist with id: " + id));
        }
    }

    @Override
    public Command command() {
        return LIST_VOCABULARIES;
    }

}
