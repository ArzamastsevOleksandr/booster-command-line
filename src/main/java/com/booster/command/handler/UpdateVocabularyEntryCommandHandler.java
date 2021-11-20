package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.params.UpdateVocabularyEntryDaoParams;
import com.booster.model.VocabularyEntry;
import com.booster.model.Word;
import com.booster.service.VocabularyEntryService;
import com.booster.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
public class UpdateVocabularyEntryCommandHandler implements CommandHandler {

    private final WordService wordService;
    private final VocabularyEntryService vocabularyEntryService;
    private final CommandLineAdapter adapter;

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

            commandWithArgs.getCorrectAnswersCount()
                    .ifPresentOrElse(params::setCorrectAnswersCount, () -> params.setCorrectAnswersCount(ve.getCorrectAnswersCount()));

            processSynonyms(commandWithArgs, ve, params);
            processAntonyms(commandWithArgs, ve, params);

            VocabularyEntry entry = vocabularyEntryService.update(params);
            adapter.writeLine(entry);
        });
    }

    private void processSynonyms(CommandWithArgs commandWithArgs, VocabularyEntry ve, UpdateVocabularyEntryDaoParams params) {
        Set<String> synonyms = commandWithArgs.getSynonyms();
        if (synonyms.isEmpty()) {
            Set<String> veSynonyms = new HashSet<>(ve.getSynonyms());
            Set<String> addSynonyms = commandWithArgs.getAddSynonyms();
            Set<String> removeSynonyms = commandWithArgs.getRemoveSynonyms();

            veSynonyms.addAll(addSynonyms);
            veSynonyms.removeAll(removeSynonyms);

            params.setSynonymIds(getWordIds(veSynonyms));
        } else {
            params.setSynonymIds(getWordIds(synonyms));
        }
    }

    private void processAntonyms(CommandWithArgs commandWithArgs, VocabularyEntry ve, UpdateVocabularyEntryDaoParams params) {
        Set<String> antonyms = commandWithArgs.getAntonyms();
        if (antonyms.isEmpty()) {
            Set<String> veAntonyms = new HashSet<>(ve.getAntonyms());
            Set<String> addAntonyms = commandWithArgs.getAddAntonyms();
            Set<String> removeAntonyms = commandWithArgs.getRemoveAntonyms();

            veAntonyms.addAll(addAntonyms);
            veAntonyms.removeAll(removeAntonyms);

            params.setAntonymIds(getWordIds(veAntonyms));
        } else {
            params.setAntonymIds(getWordIds(antonyms));
        }
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
