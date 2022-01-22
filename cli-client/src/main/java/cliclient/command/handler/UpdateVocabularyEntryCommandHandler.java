package cliclient.command.handler;

import api.vocabulary.PatchVocabularyEntryInput;
import api.vocabulary.VocabularyEntryDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.UpdateVocabularyEntryCommandArgs;
import cliclient.dao.params.UpdateVocabularyEntryDaoParams;
import cliclient.feign.vocabulary.VocabularyEntryControllerApiClient;
import cliclient.model.VocabularyEntry;
import cliclient.model.Word;
import cliclient.service.ColorProcessor;
import cliclient.service.VocabularyEntryService;
import cliclient.service.WordService;
import cliclient.util.StringUtil;
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
    private final VocabularyEntryControllerApiClient vocabularyEntryControllerApiClient;
    private final CommandLineAdapter adapter;
    private final ColorProcessor colorProcessor;
    private final StringUtil stringUtil;

    // todo: update contexts
    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (UpdateVocabularyEntryCommandArgs) commandArgs;

        var input = new PatchVocabularyEntryInput();

        input.setId(args.getId());
        args.getName().ifPresent(input::setName);
        args.getDefinition().ifPresent(input::setDefinition);
        args.getCorrectAnswersCount().ifPresent(input::setCorrectAnswersCount);

        // todo: support updating synonyms
        VocabularyEntryDto vocabularyEntryDto = vocabularyEntryControllerApiClient.patchEntry(input);
        adapter.writeLine(vocabularyEntryDto);
    }

    private void processSynonyms(UpdateVocabularyEntryCommandArgs args, VocabularyEntry ve, UpdateVocabularyEntryDaoParams params) {
        Set<String> synonyms = args.getSynonyms();
        if (synonyms.isEmpty()) {
            Set<String> veSynonyms = new HashSet<>(ve.getSynonyms());
            Set<String> addSynonyms = args.getAddSynonyms();
            Set<String> removeSynonyms = args.getRemoveSynonyms();

            veSynonyms.addAll(addSynonyms);
            veSynonyms.removeAll(removeSynonyms);

            params.setSynonymIds(getWordIds(veSynonyms));
        } else {
            params.setSynonymIds(getWordIds(synonyms));
        }
    }

    private void processAntonyms(UpdateVocabularyEntryCommandArgs args, VocabularyEntry ve, UpdateVocabularyEntryDaoParams params) {
        Set<String> antonyms = args.getAntonyms();
        if (antonyms.isEmpty()) {
            Set<String> veAntonyms = new HashSet<>(ve.getAntonyms());
            Set<String> addAntonyms = args.getAddAntonyms();
            Set<String> removeAntonyms = args.getRemoveAntonyms();

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
                .filter(stringUtil::isNotBlank)
                .map(wordService::findByNameOrCreateAndGet)
                .map(Word::getId)
                .collect(toSet());
    }

}
