package cliclient.command.arguments.validator;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static cliclient.command.Command.LIST_VOCABULARY_ENTRIES;

@Component
@RequiredArgsConstructor
public class ListVocabularyEntriesArgValidator implements ArgValidator {

    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
//        commandWithArgs.getId().ifPresentOrElse(id -> {
//            checkIfVocabularyEntryExistsWithId(id);
//            checkIfSubstringFlagIsUsed(commandWithArgs);
//        }, () -> {
//            commandWithArgs.getSubstring()
//                    .ifPresentOrElse(this::checkIfEntriesExistWithSubstring, this::checkIfAnyEntriesExist);
//        });
        return commandWithArgs;
    }

    private void checkIfVocabularyEntryExistsWithId(long id) {
        if (!vocabularyEntryService.existsWithId(id)) {
            throw new ArgsValidationException("Vocabulary entry does not exist with id: " + id);
        }
    }

    private void checkIfSubstringFlagIsUsed(CommandWithArgs commandWithArgs) {
        commandWithArgs.getSubstring().ifPresent(s -> {
            throw new ArgsValidationException("Only one of id or substring flags can be used.");
        });
    }

    private void checkIfEntriesExistWithSubstring(String substring) {
        if (!vocabularyEntryService.existAnyWithSubstring(substring)) {
            throw new ArgsValidationException("No entries exist with substring " + substring);
        }
    }

    private void checkIfAnyEntriesExist() {
        if (!vocabularyEntryService.existAny()) {
            throw new ArgsValidationException("There are no vocabulary entries in the system yet.");
        }
    }

    @Override
    public Command command() {
        return LIST_VOCABULARY_ENTRIES;
    }

}
