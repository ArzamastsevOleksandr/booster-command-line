package com.booster.command.arguments.resolver;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.ListVocabularyEntriesArgs;
import com.booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.booster.command.Command.LIST_VOCABULARY_ENTRIES;

@Component
@RequiredArgsConstructor
public class ListVocabularyEntriesArgsResolver implements ArgsResolver {

    private static final String ID_FLAG = "id";

    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public CommandWithArguments resolve(List<String> args) {
        var builder = getCommandBuilder();
        try {
            Map<String, String> flag2value = checkFlagsWithValuesAndReturn(args);
            if (flag2value.containsKey(ID_FLAG)) {
                checkIfIdIsCorrectNumber(flag2value.get(ID_FLAG));
                checkIfVocabularyEntryExistsWithId(Long.parseLong(flag2value.get(ID_FLAG)));
                return builder
                        .args(new ListVocabularyEntriesArgs(Long.parseLong(flag2value.get(ID_FLAG))))
                        .build();
            } else {
                return builder
                        .args(ListVocabularyEntriesArgs.empty())
                        .build();
            }
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
        return LIST_VOCABULARY_ENTRIES;
    }

}
