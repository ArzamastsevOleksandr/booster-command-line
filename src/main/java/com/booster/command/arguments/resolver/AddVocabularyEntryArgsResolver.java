package com.booster.command.arguments.resolver;

import com.booster.command.arguments.AddVocabularyEntryArgs;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.VocabularyDao;
import com.booster.dao.WordDao;
import com.booster.model.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.booster.command.Command.ADD_VOCABULARY_ENTRY;
import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class AddVocabularyEntryArgsResolver implements ArgsResolver {

    private static final String NAME_FLAG = "n";
    private static final String ID_FLAG = "id";

    private final VocabularyDao vocabularyDao;
    private final WordDao wordDao;

    public CommandWithArguments resolve(List<String> args) {
        CommandWithArguments.CommandWithArgumentsBuilder builder = getBuilder();
        try {
            checkIfArgumentsAreSpecified(args);

            Map<String, String> flag2value = checkFlagsWithValuesAndReturn(args);

            checkIfMandatoryFlagsArePresent(flag2value, Set.of(NAME_FLAG, ID_FLAG));
            checkIfIdIsCorrectNumber(flag2value.get(ID_FLAG));
            checkIfVocabularyExistsWithId(Long.parseLong(flag2value.get(ID_FLAG)));
            long wordId = getWordIdByWordName(flag2value.get(NAME_FLAG));
            List<Long> synonymIds = getSynonymIds(flag2value);
            List<Long> antonymIds = getAntonymIds(flag2value);

            return builder
                    .args(new AddVocabularyEntryArgs(wordId, Long.parseLong(flag2value.get(ID_FLAG)), synonymIds, antonymIds))
                    .build();
        } catch (ArgsValidationException e) {
            return builder
                    .argErrors(e.getArgErrors())
                    .build();
        }
    }

    private List<Long> getSynonymIds(Map<String, String> flag2value) {
        return Optional.ofNullable(flag2value.get("s"))
                .map(this::getWordIds)
                .orElse(List.of());
    }

    private List<Long> getAntonymIds(Map<String, String> flag2value) {
        return Optional.ofNullable(flag2value.get("a"))
                .map(this::getWordIds)
                .orElse(List.of());
    }

    private List<Long> getWordIds(String values) {
        return Arrays.stream(values.split(";"))
                .map(String::strip)
                .map(wordDao::findByNameOrCreateAndGet)
                .map(Word::getId)
                .collect(toList());
    }

    private long getWordIdByWordName(String name) {
        return wordDao.findByNameOrCreateAndGet(name).getId();
    }

    @Override
    public String commandString() {
        return ADD_VOCABULARY_ENTRY.extendedToString();
    }

    private CommandWithArguments.CommandWithArgumentsBuilder getBuilder() {
        return CommandWithArguments.builder()
                .command(ADD_VOCABULARY_ENTRY);
    }

    private void checkIfVocabularyExistsWithId(long id) {
        if (!vocabularyDao.existsWithId(id)) {
            throw new ArgsValidationException(List.of("Vocabulary with id: " + id + " does not exist."));
        }
    }

}
