package com.booster.command.arguments.resolver;

import com.booster.command.Command;
import com.booster.command.arguments.AddVocabularyEntryArgs;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.VocabularyDao;
import com.booster.dao.VocabularyEntryDao;
import com.booster.model.Word;
import com.booster.service.WordService;
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
    public static final String DEFINITION = "d";

    private final VocabularyEntryDao vocabularyEntryDao;
    private final VocabularyDao vocabularyDao;
    private final WordService wordService;

    public CommandWithArguments resolve(List<String> args) {
        CommandWithArguments.CommandWithArgumentsBuilder builder = getCommandBuilder();
        try {
            checkIfArgumentsAreSpecified(args);

            Map<String, String> flag2value = checkFlagsWithValuesAndReturn(args);

            checkIfMandatoryFlagsArePresent(flag2value, Set.of(NAME_FLAG, ID_FLAG));
            checkIfIdIsCorrectNumber(flag2value.get(ID_FLAG));
            checkIfVocabularyExistsWithId(Long.parseLong(flag2value.get(ID_FLAG)));
            long wordId = getWordIdByWordName(flag2value.get(NAME_FLAG));
            checkIfVocabularyEntryAlreadyExistsWithWordForVocabulary(wordId, Long.parseLong(flag2value.get(ID_FLAG)));
            List<Long> synonymIds = getSynonymIds(flag2value);
            List<Long> antonymIds = getAntonymIds(flag2value);
            String definition = getDefinition(flag2value);

            return builder
                    .args(AddVocabularyEntryArgs.builder()
                            .wordId(wordId)
                            .vocabularyId(Long.parseLong(flag2value.get(ID_FLAG)))
                            .synonymIds(synonymIds)
                            .antonymIds(antonymIds)
                            .definition(definition)
                            .build())
                    .build();
        } catch (ArgsValidationException e) {
            return builder
                    .argErrors(e.getArgErrors())
                    .build();
        }
    }

    @Override
    public Command command() {
        return ADD_VOCABULARY_ENTRY;
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
                .map(wordService::findByNameOrCreateAndGet)
                .map(Word::getId)
                .collect(toList());
    }

    private long getWordIdByWordName(String name) {
        return wordService.findByNameOrCreateAndGet(name).getId();
    }

    private void checkIfVocabularyEntryAlreadyExistsWithWordForVocabulary(long wordId, long vocabularyId) {
        if (vocabularyEntryDao.existsWithWordIdAndVocabularyId(wordId, vocabularyId)) {
            throw new ArgsValidationException(List.of("Vocabulary entry already exists in vocabulary with id: " + vocabularyId));
        }
    }

    private void checkIfVocabularyExistsWithId(long id) {
        if (!vocabularyDao.existsWithId(id)) {
            throw new ArgsValidationException(List.of("Vocabulary with id: " + id + " does not exist."));
        }
    }

    private String getDefinition(Map<String, String> flag2value) {
        return flag2value.get(DEFINITION);
    }

}
