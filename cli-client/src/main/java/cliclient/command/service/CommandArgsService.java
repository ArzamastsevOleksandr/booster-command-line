package cliclient.command.service;

import cliclient.command.Command;
import cliclient.command.arguments.*;
import cliclient.exception.IncorrectCommandFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
class CommandArgsService {

    CommandArgs getCommandArgs(CommandWithArgs cwa) {
        CommandArgsResult commandArgsResult = switch (cwa.getCommand()) {
            case EXIT, NO_INPUT, LIST_FLAG_TYPES, LIST_LANGUAGES, DELETE_SETTINGS, LIST_TAGS, SHOW_SETTINGS, UNRECOGNIZED -> CommandArgsResult.empty();
            case HELP -> CommandArgsResult.success(new HelpCommandArgs(cwa.getHelpTarget()));
            case ADD_LANGUAGE -> addLanguage(cwa);
            case DELETE_LANGUAGE -> deleteLanguage(cwa);
            case LIST_VOCABULARY_ENTRIES -> CommandArgsResult.success(new ListVocabularyEntriesCommandArgs(ofNullable(cwa.getId()), cwa.getPagination(), ofNullable(cwa.getSubstring())));
            case DELETE_VOCABULARY_ENTRY -> deleteVocabularyEntry(cwa);
            case ADD_VOCABULARY_ENTRY -> addVocabularyEntry(cwa);
            case UPDATE_VOCABULARY_ENTRY -> updateVocabularyEntry(cwa);
            case START_VOCABULARY_TRAINING_SESSION -> CommandArgsResult.success(new StartVocabularyTrainingSessionCommandArgs(cwa.getMode(), cwa.getEntriesPerVocabularyTrainingSession()));
            case DOWNLOAD -> CommandArgsResult.success(new DownloadCommandArgs(cwa.getFilename()));
            case UPLOAD -> CommandArgsResult.success(new UploadCommandArgs(cwa.getFilename()));
            case ADD_SETTINGS -> CommandArgsResult.success(AddSettingsCommandArgs.builder()

                    .defaultLanguageId(cwa.getLanguageId())
                    .defaultLanguageName(cwa.getLanguageName())

                    .entriesPerVocabularyTrainingSession(cwa.getEntriesPerVocabularyTrainingSession())

                    .languagesPagination(cwa.getLanguagesPagination())
                    .notesPagination(cwa.getNotesPagination())
                    .tagsPagination(cwa.getTagsPagination())
                    .vocabularyPagination(cwa.getVocabularyPagination())

                    .build());
            case LIST_NOTES -> CommandArgsResult.success(new ListNotesCommandArgs(ofNullable(cwa.getId()), cwa.getPagination()));
            case ADD_NOTE -> addNote(cwa);
            case DELETE_NOTE -> deleteNote(cwa);
            case ADD_TAG -> addTag(cwa);
            case USE_TAG -> useTag(cwa);
        };
        commandArgsResult.validate();
        return commandArgsResult.commandArgs;
    }

    private CommandArgsResult addTag(CommandWithArgs cwa) {
        if (cwa.getName() == null) {
            return CommandArgsResult.withErrors("Name is missing", Command.ADD_TAG);
        }
        return CommandArgsResult.success(new AddTagCommandArgs(cwa.getName()));
    }

    private CommandArgsResult deleteNote(CommandWithArgs cwa) {
        if (cwa.getId() == null) {
            return CommandArgsResult.withErrors("Id is missing", Command.DELETE_NOTE);
        }
        return CommandArgsResult.success(new DeleteNoteCommandArgs(cwa.getId()));
    }

    private CommandArgsResult addNote(CommandWithArgs cwa) {
        if (cwa.getContent() == null) {
            return CommandArgsResult.withErrors("Content is missing", Command.ADD_NOTE);
        }
        return CommandArgsResult.success(new AddNoteCommandArgs(cwa.getContent(), ofNullable(cwa.getTag())
                .map(Set::of)
                .orElse(Set.of())));
    }

    private CommandArgsResult updateVocabularyEntry(CommandWithArgs cwa) {
        if (cwa.getId() == null) {
            return CommandArgsResult.withErrors("Id is missing", Command.UPDATE_VOCABULARY_ENTRY);
        }
        var commandArgs = UpdateVocabularyEntryCommandArgs.builder()
                .id(cwa.getId())
                .name(cwa.getName())
                .definition(cwa.getDefinition())
                .correctAnswersCount(cwa.getCorrectAnswersCount())
                .antonyms(cwa.getAntonyms())
                .synonyms(cwa.getSynonyms())
                .addAntonyms(cwa.getAddAntonyms())
                .removeAntonyms(cwa.getRemoveAntonyms())
                .addSynonyms(cwa.getAddSynonyms())
                .removeSynonyms(cwa.getRemoveSynonyms())
                .build();
        return CommandArgsResult.success(commandArgs);
    }

    private CommandArgsResult addVocabularyEntry(CommandWithArgs cwa) {
        if (cwa.getName() == null) {
            return CommandArgsResult.withErrors("Name is missing", Command.ADD_VOCABULARY_ENTRY);
        }
        var commandArgs = AddVocabularyEntryCommandArgs.builder()
                .name(cwa.getName())
                .languageId(cwa.getLanguageId())
                .definition(cwa.getDefinition())
                .tag(cwa.getTag())
                .antonyms(cwa.getAntonyms())
                .synonyms(cwa.getSynonyms())
                .contexts(cwa.getContexts())
                .build();
        return CommandArgsResult.success(commandArgs);
    }

    private CommandArgsResult deleteVocabularyEntry(CommandWithArgs cwa) {
        if (cwa.getId() == null) {
            return CommandArgsResult.withErrors("Id is missing", Command.DELETE_VOCABULARY_ENTRY);
        }
        return CommandArgsResult.success(new DeleteVocabularyEntryCommandArgs(cwa.getId()));
    }

    private CommandArgsResult addLanguage(CommandWithArgs cwa) {
        if (cwa.getName() == null) {
            return CommandArgsResult.withErrors("Name is missing", Command.ADD_LANGUAGE);
        }
        return CommandArgsResult.success(new AddLanguageCommandArgs(cwa.getName()));
    }

    private CommandArgsResult deleteLanguage(CommandWithArgs cwa) {
        if (cwa.getId() == null) {
            return CommandArgsResult.withErrors("Id is missing", Command.DELETE_LANGUAGE);
        }
        return CommandArgsResult.success(new DeleteLanguageCommandArgs(cwa.getId()));
    }

    private CommandArgsResult useTag(CommandWithArgs cwa) {
        if (cwa.getNoteId() == null) {
            return CommandArgsResult.withErrors("Note id is missing", Command.USE_TAG);
        }
        return CommandArgsResult.success(new UseTagCommandArgs(cwa.getTag(), cwa.getNoteId()));
    }

    private record CommandArgsResult(Command command, CommandArgs commandArgs, String error) {

        static CommandArgsResult success(CommandArgs commandArgs) {
            return new CommandArgsResult(null, commandArgs, null);
        }

        static CommandArgsResult withErrors(String error, Command command) {
            return new CommandArgsResult(command, null, error);
        }

        static CommandArgsResult empty() {
            return new CommandArgsResult(null, new EmptyCommandArgs(), null);
        }

        void validate() {
            if (error != null) {
                throw new IncorrectCommandFormatException(error, command);
            }
        }
    }

}
