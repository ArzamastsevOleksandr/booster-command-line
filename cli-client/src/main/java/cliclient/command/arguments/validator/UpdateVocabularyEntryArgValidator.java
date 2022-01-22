package cliclient.command.arguments.validator;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

import static cliclient.command.Command.UPDATE_VOCABULARY_ENTRY;

@Component
@RequiredArgsConstructor
public class UpdateVocabularyEntryArgValidator implements ArgValidator {

    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
//        commandWithArgs.getId().ifPresentOrElse(this::checkIfVocabularyEntryExistsWithId, ID_IS_MISSING);
//
//        checkAntonymParameters(commandWithArgs);
//        checkSynonymParameters(commandWithArgs);

        return commandWithArgs;
    }

    @Override
    public Command command() {
        return UPDATE_VOCABULARY_ENTRY;
    }

    private void checkIfVocabularyEntryExistsWithId(Long id) {
        if (!vocabularyEntryService.existsWithId(id)) {
            throw new ArgsValidationException("Vocabulary entry does not exist with id: " + id);
        }
    }

    private void checkAntonymParameters(CommandWithArgs commandWithArgs) {
        Set<String> addAntonyms = commandWithArgs.getAddAntonyms();
        Set<String> antonyms = commandWithArgs.getAntonyms();
        Set<String> removeAntonyms = commandWithArgs.getRemoveAntonyms();
        if (!antonyms.isEmpty() && !addAntonyms.isEmpty()) {
            throw new ArgsValidationException("Overriding and adding antonyms at the same time is forbidden");
        }
        if (!antonyms.isEmpty() && !removeAntonyms.isEmpty()) {
            throw new ArgsValidationException("Overriding and removing antonyms at the same time is forbidden");
        }
    }

    private void checkSynonymParameters(CommandWithArgs commandWithArgs) {
        Set<String> synonyms = commandWithArgs.getSynonyms();
        Set<String> addSynonyms = commandWithArgs.getAddSynonyms();
        Set<String> removeSynonyms = commandWithArgs.getRemoveSynonyms();
        if (!synonyms.isEmpty() && !addSynonyms.isEmpty()) {
            throw new ArgsValidationException("Overriding and adding synonyms at the same time is forbidden");
        }
        if (!synonyms.isEmpty() && !removeSynonyms.isEmpty()) {
            throw new ArgsValidationException("Overriding and removing synonyms at the same time is forbidden");
        }
    }

}
