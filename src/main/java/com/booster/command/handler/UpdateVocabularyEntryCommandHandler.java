package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.VocabularyEntryDao;
import com.booster.dao.params.UpdateVocabularyEntryDaoParams;
import com.booster.model.Word;
import com.booster.service.VocabularyEntryService;
import com.booster.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
public class UpdateVocabularyEntryCommandHandler implements CommandHandler {

    private final VocabularyEntryDao vocabularyEntryDao;
    private final WordService wordService;
    private final VocabularyEntryService vocabularyEntryService;

    // todo: flag for cac?
    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getId().flatMap(vocabularyEntryService::findById).ifPresent(ve -> {
            var params = new UpdateVocabularyEntryDaoParams();
            params.setId(ve.getId());

            commandWithArgs.getName().ifPresentOrElse(name -> {
                Word updatedWord = wordService.findByNameOrCreateAndGet(name);
                params.setWordId(updatedWord.getId());
            }, () -> params.setWordId(ve.getWordId()));

            commandWithArgs.getDefinition()
                    .ifPresentOrElse(params::setDefinition, () -> ve.getDefinition().ifPresent(params::setDefinition));

            Set<String> synonyms = commandWithArgs.getSynonyms();
            if (synonyms.isEmpty()) {
                params.setSynonymIds(getWordIds(ve.getSynonyms()));
            } else {
                params.setSynonymIds(getWordIds(synonyms));
            }

            Set<String> antonyms = commandWithArgs.getAntonyms();
            if (antonyms.isEmpty()) {
                params.setAntonymIds(getWordIds(ve.getAntonyms()));
            } else {
                params.setAntonymIds(getWordIds(antonyms));
            }
            vocabularyEntryDao.update(params);
        });
    }

    @Override
    public Command getCommand() {
        return Command.UPDATE_VOCABULARY_ENTRY;
    }

    private Set<Long> getWordIds(Set<String> words) {
        return words.stream()
                .filter(s -> !s.isBlank())
                .map(wordService::findByNameOrCreateAndGet)
                .map(Word::getId)
                .collect(toSet());
    }

}
