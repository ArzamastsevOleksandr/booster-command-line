package cliclient.parser;

import api.settings.SettingsApi;
import api.settings.SettingsDto;
import cliclient.command.Command;
import cliclient.command.FlagType;
import cliclient.command.args.*;
import cliclient.config.PropertyHolder;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static cliclient.command.Command.*;
import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
class CommandWithArgsService {

    private final SettingsApi settingsApi;
    private final PropertyHolder propertyHolder;

    CmdArgs toCommandWithArgs(List<Token> tokens, ValidationResult validationResult) {
        return switch (validationResult.commandValidationResult) {
            case NO_INPUT -> new NoInputCmdArgs();
            case MUST_START_WITH_COMMAND -> new ErroneousCmdArgs(null, "Input must start with command, got: " + validationResult.comment);
            case SINGLE_COMMAND -> singleCommand(Command.fromString(tokens.get(0).value()));
            case COMMAND_MUST_BE_FOLLOWED_BY_FLAG_ARGUMENT_PAIRS -> new ErroneousCmdArgs(Command.fromString(tokens.get(0).value()), "Command must be followed by a sequence of \\flag=value pairs");
            case HELP_ON_COMMAND -> new HelpCmdArgs(Command.fromString(tokens.get(1).value()));
            case HELP_ON_TEXT -> new ErroneousCmdArgs(Command.HELP, "Help used for unknown command: " + validationResult.comment);
            case HELP_EXPECTED_BUT_GOT_OTHER_COMMAND_INSTEAD -> new ErroneousCmdArgs(Command.HELP, "Command must be followed by \\flag=value pairs");
            case FORBIDDEN_FLAG_VALUE -> new ErroneousCmdArgs(Command.fromString(tokens.get(0).value()), validationResult.comment);
            case COMMAND_WITH_FLAG_VALUE_PAIRS -> commandWithFlagValuePairs(tokens);
        };
    }

    private CmdArgs singleCommand(Command command) {
        return switch (command) {
            case HELP -> new HelpCmdArgs(null);
            case LIST_AVAILABLE_LANGUAGES -> new ListAvailableLanguagesCmdWithArgs();
            case SHOW_SETTINGS -> new ShowSettingsCmdWithArgs();
            case DELETE_SETTINGS -> new DeleteSettingsCmdArgs();
            case LIST_TAGS -> new ListTagsCmdWithArgs();
            case LIST_FLAG_TYPES -> new ListFlagTypesCmdWithArgs();
            case EXIT -> new ExitCmdArgs();
            case LIST_VOCABULARY_ENTRIES -> listVocabularyEntries();
            case DELETE_VOCABULARY_ENTRY -> new ErroneousCmdArgs(DELETE_VOCABULARY_ENTRY, "Id is missing");
            case ADD_VOCABULARY_ENTRY -> new ErroneousCmdArgs(ADD_VOCABULARY_ENTRY, "Name is missing");
            case UPDATE_VOCABULARY_ENTRY -> new ErroneousCmdArgs(UPDATE_VOCABULARY_ENTRY, "Id is missing");
            case START_VOCABULARY_TRAINING_SESSION -> startVocabularyTrainingSession();
            case DOWNLOAD -> download();
            case UPLOAD -> upload();
            case ADD_SETTINGS -> addSettings();
            case LIST_NOTES -> listNotes();
            case ADD_NOTE -> new ErroneousCmdArgs(ADD_NOTE, "Content is missing");
            case DELETE_NOTE -> new ErroneousCmdArgs(DELETE_NOTE, "Id is missing");
            case ADD_TAG -> new ErroneousCmdArgs(ADD_TAG, "Name is missing");
            case USE_TAG -> new ErroneousCmdArgs(USE_TAG, "Note id is missing");
            default -> throw new IllegalStateException("Unexpected value: " + command);
        };
    }

    private CmdArgs listNotes() {
        Integer pagination = findSettingsIgnoringNotFound().map(SettingsDto::getNotesPagination).orElse(propertyHolder.getDefaultPagination());
        return new ListNotesCmdArgs(null, pagination);
    }

    private CmdArgs addSettings() {
        return AddSettingsCmdArgs.builder()
                .defaultLanguageName(null)
                .languagesPagination(propertyHolder.getDefaultPagination())
                .notesPagination(propertyHolder.getDefaultPagination())
                .vocabularyPagination(propertyHolder.getDefaultPagination())
                .tagsPagination(propertyHolder.getDefaultPagination())
                .entriesPerVocabularyTrainingSession(propertyHolder.getEntriesPerVocabularyTrainingSession())
                .build();
    }

    private CmdArgs upload() {
        return new UploadCmdArgs(propertyHolder.getUploadFilename());
    }

    private CmdArgs download() {
        return new DownloadCmdArgs(propertyHolder.getDownloadFilename());
    }

    private CmdArgs startVocabularyTrainingSession() {
        return new StartVocabularyTrainingSessionCmdArgs(VocabularyTrainingSessionMode.SYNONYMS, resolveVocabularyTrainingSessionSize());
    }

    private Integer resolveVocabularyTrainingSessionSize() {
        return findSettingsIgnoringNotFound()
                .map(SettingsDto::getEntriesPerVocabularyTrainingSession)
                .orElse(propertyHolder.getEntriesPerVocabularyTrainingSession());
    }

    private CmdArgs listVocabularyEntries() {
        return ListVocabularyEntriesCmdWithArgs.builder()
                .pagination(findSettingsIgnoringNotFound().map(SettingsDto::getVocabularyPagination).orElse(propertyHolder.getDefaultPagination()))
                .build();
    }

    private Optional<SettingsDto> findSettingsIgnoringNotFound() {
        try {
            return Optional.of(settingsApi.findOne());
        } catch (FeignException.FeignClientException ex) {
            if (ex.status() == HttpStatus.NOT_FOUND.value()) {
                return Optional.empty();
            }
            throw ex;
        }
    }

    private CmdArgs commandWithFlagValuePairs(List<Token> tokens) {
        CommandWithArgs cwa = constructCommandWithArgs(tokens);

        return toNewCommandWithArgs(cwa);
    }

    private CmdArgs toNewCommandWithArgs(CommandWithArgs cwa) {
        return switch (cwa.getCommand()) {
            case LIST_VOCABULARY_ENTRIES -> listVocabularyEntriesWithParameters(cwa);
            case DELETE_VOCABULARY_ENTRY -> deleteVocabularyEntryWithParams(cwa);
            case ADD_VOCABULARY_ENTRY -> addVocabularyEntryWithParams(cwa);
            case UPDATE_VOCABULARY_ENTRY -> updateVocabularyEntryWithParams(cwa);
            case START_VOCABULARY_TRAINING_SESSION -> startVocabularyTrainingSessionWithParams(cwa);
            case DOWNLOAD -> new DownloadCmdArgs(cwa.getFilename());
            case UPLOAD -> new UploadCmdArgs(cwa.getFilename());
            case ADD_SETTINGS -> addSettingsWithParams(cwa);
            case LIST_NOTES -> listNotesWithParams(cwa);
            case ADD_NOTE -> addNoteWithParams(cwa);
            case DELETE_NOTE -> deleteNoteWithParams(cwa);
            case ADD_TAG -> addTagWithParams(cwa);
            case USE_TAG -> useTagWithParams(cwa);
            // todo: handle globally with no program interruption
            default -> throw new IllegalStateException("Command does not support \\flag=value pairs: " + cwa.getCommand());
        };
    }

    private CmdArgs useTagWithParams(CommandWithArgs cwa) {
        if (cwa.getNoteId() == null) {
            return new ErroneousCmdArgs(USE_TAG, "Note id is missing");
        }
        return new UseTagCmdArgs(cwa.getNoteId(), cwa.getTag());
    }

    private CmdArgs addTagWithParams(CommandWithArgs cwa) {
        if (cwa.getName() == null) {
            return new ErroneousCmdArgs(ADD_TAG, "Name is missing");
        }
        return new AddTagCmdArgs(cwa.getName());
    }

    private CmdArgs deleteNoteWithParams(CommandWithArgs cwa) {
        if (cwa.getId() == null) {
            return new ErroneousCmdArgs(DELETE_NOTE, "Id is missing");
        }
        return new DeleteNoteCmdArgs(cwa.getId());
    }

    private CmdArgs addNoteWithParams(CommandWithArgs cwa) {
        if (cwa.getContent() == null) {
            return new ErroneousCmdArgs(ADD_NOTE, "Content is missing");
        }
        return new AddNoteCmdArgs(cwa.getContent(), cwa.getTag());
    }

    private CmdArgs listNotesWithParams(CommandWithArgs cwa) {
        Integer pagination = Optional.ofNullable(cwa.getPagination())
                .orElse(findSettingsIgnoringNotFound().map(SettingsDto::getNotesPagination).orElse(propertyHolder.getDefaultPagination()));
        return new ListNotesCmdArgs(cwa.getId(), pagination);
    }

    private AddSettingsCmdArgs addSettingsWithParams(CommandWithArgs cwa) {
        return AddSettingsCmdArgs.builder()
                .defaultLanguageName(cwa.getLanguageName())
                .entriesPerVocabularyTrainingSession(cwa.getEntriesPerVocabularyTrainingSession())
                .vocabularyPagination(cwa.getVocabularyPagination())
                .tagsPagination(cwa.getTagsPagination())
                .notesPagination(cwa.getNotesPagination())
                .languagesPagination(cwa.getLanguagesPagination())
                .build();
    }

    private CmdArgs startVocabularyTrainingSessionWithParams(CommandWithArgs cwa) {
        var mode = cwa.getMode() == null
                ? VocabularyTrainingSessionMode.getDefaultMode()
                : cwa.getMode();
        var sessionSize = cwa.getEntriesPerVocabularyTrainingSession() == null
                ? resolveVocabularyTrainingSessionSize()
                : cwa.getEntriesPerVocabularyTrainingSession();
        return new StartVocabularyTrainingSessionCmdArgs(mode, sessionSize);
    }

    private CmdArgs updateVocabularyEntryWithParams(CommandWithArgs cwa) {
        if (cwa.getId() == null) {
            return new ErroneousCmdArgs(UPDATE_VOCABULARY_ENTRY, "Id is missing");
        }
        return UpdateVocabularyEntryCmdArgs.builder()
                .id(cwa.getId())
                .name(cwa.getName())
                .correctAnswersCount(cwa.getCorrectAnswersCount())
                .definition(cwa.getDefinition())
                .addAntonyms(cwa.getAddAntonyms())
                .removeAntonyms(cwa.getRemoveAntonyms())
                .addSynonyms(cwa.getAddSynonyms())
                .removeSynonyms(cwa.getRemoveSynonyms())
                .antonyms(cwa.getAntonyms())
                .synonyms(cwa.getSynonyms())
                .build();
    }

    private CmdArgs addVocabularyEntryWithParams(CommandWithArgs cwa) {
        if (cwa.getName() == null) {
            return new ErroneousCmdArgs(ADD_VOCABULARY_ENTRY, "Name is missing");
        }
        return AddVocabularyEntryCmdArgs.builder()
                .name(cwa.getName())
                .language(cwa.getLanguageName())
                .tag(cwa.getTag())
                .definition(cwa.getDefinition())
                .contexts(cwa.getContexts())
                .synonyms(cwa.getSynonyms())
                .antonyms(cwa.getAntonyms())
                .translations(cwa.getTranslations())
                .build();
    }

    private CmdArgs deleteVocabularyEntryWithParams(CommandWithArgs cwa) {
        if (cwa.getId() == null) {
            return new ErroneousCmdArgs(DELETE_VOCABULARY_ENTRY, "Id is missing");
        }
        return new DeleteVocabularyEntryCmdArgs(cwa.getId());
    }

    private CmdArgs listVocabularyEntriesWithParameters(CommandWithArgs cwa) {
        return ListVocabularyEntriesCmdWithArgs.builder()
                .id(cwa.getId())
                .pagination(Optional.ofNullable(cwa.getPagination()).orElse(findSettingsIgnoringNotFound().map(SettingsDto::getVocabularyPagination).orElse(propertyHolder.getDefaultPagination())))
                .substring(cwa.getSubstring())
                .build();
    }

    private CommandWithArgs constructCommandWithArgs(List<Token> tokens) {
        var cwaBuilder = CommandWithArgs.builder().command(Command.fromString(tokens.get(0).value()));
        List<Token> flagValuePairs = tokens.subList(1, tokens.size());
        for (int i = 0; i < flagValuePairs.size(); i += 3) {
            Token flag = flagValuePairs.get(i);
            Token value = flagValuePairs.get(i + 2);

            var flagType = FlagType.fromString(flag.value());
            String flagValue = value.value();
            cwaBuilder = switch (flagType) {
                case ID -> cwaBuilder.id(Long.parseLong(flagValue));
                case NOTE_ID -> cwaBuilder.noteId(Long.parseLong(flagValue));
                case VOCABULARY_ENTRY_ID -> cwaBuilder.vocabularyEntryId(Long.parseLong(flagValue));
                case NAME -> cwaBuilder.name(flagValue);
                case LANGUAGE_NAME -> cwaBuilder.languageName(flagValue);
                case TAG -> cwaBuilder.tag(flagValue);
                case DEFINITION -> cwaBuilder.definition(flagValue);
                case FILE -> cwaBuilder.filename(flagValue);
                case MODE_VOCABULARY -> cwaBuilder.mode(VocabularyTrainingSessionMode.fromString(flagValue));
                case SYNONYMS -> cwaBuilder.synonyms(getWordEquivalentNames(flagValue));
                case ANTONYMS -> cwaBuilder.antonyms(getWordEquivalentNames(flagValue));
                case CONTENT -> cwaBuilder.content(flagValue);
                case CORRECT_ANSWERS_COUNT -> cwaBuilder.correctAnswersCount(Integer.parseInt(flagValue));
                case ADD_ANTONYMS -> cwaBuilder.addAntonyms(getWordEquivalentNames(flagValue));
                case ADD_SYNONYMS -> cwaBuilder.addSynonyms(getWordEquivalentNames(flagValue));
                case REMOVE_ANTONYMS -> cwaBuilder.removeAntonyms(getWordEquivalentNames(flagValue));
                case REMOVE_SYNONYMS -> cwaBuilder.removeSynonyms(getWordEquivalentNames(flagValue));
                case PAGINATION -> cwaBuilder.pagination(Integer.parseInt(flagValue));
                case LANGUAGES_PAGINATION -> cwaBuilder.languagesPagination(Integer.parseInt(flagValue));
                case NOTES_PAGINATION -> cwaBuilder.notesPagination(Integer.parseInt(flagValue));
                case TAGS_PAGINATION -> cwaBuilder.tagsPagination(Integer.parseInt(flagValue));
                case VOCABULARY_PAGINATION -> cwaBuilder.vocabularyPagination(Integer.parseInt(flagValue));
                case SUBSTRING -> cwaBuilder.substring(flagValue);
                case CONTEXTS -> cwaBuilder.contexts(getContexts(flagValue));
                case ENTRIES_PER_VOCABULARY_TRAINING_SESSION -> cwaBuilder.entriesPerVocabularyTrainingSession(Integer.parseInt(flagValue));
                case TRANSLATIONS -> cwaBuilder.translations(getWordEquivalentNames(flagValue));
                default -> throw new RuntimeException("Flag does not have a handler: " + flagType);
            };
        }
        return cwaBuilder.build();
    }

    private Set<String> getContexts(String value) {
        return Arrays.stream(value.split(Token.CONTEXT_DELIMITER))
                .map(String::strip)
                .filter(this::isNotBlank)
                .collect(toSet());
    }

    private Set<String> getWordEquivalentNames(String value) {
        return Arrays.stream(value.split(Token.WORD_EQUIVALENT_DELIMITER))
                .map(String::strip)
                .filter(this::isNotBlank)
                .collect(toSet());
    }

    private boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }

}
