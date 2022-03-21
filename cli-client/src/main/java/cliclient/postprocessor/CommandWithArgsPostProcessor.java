package cliclient.postprocessor;

import api.settings.SettingsApi;
import api.settings.SettingsDto;
import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.command.arguments.VocabularyTrainingSessionMode;
import cliclient.config.PropertyHolder;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class CommandWithArgsPostProcessor {

    private final SettingsApi settingsApi;
    private final PropertyHolder propertyHolder;

    public CommandWithArgs process(CommandWithArgs cwa) {
        Command command = cwa.getCommand();
        return switch (command) {
            case LIST_VOCABULARY_ENTRIES -> resolvePagination(cwa, SettingsDto::getVocabularyPagination);
            case LIST_NOTES -> resolvePagination(cwa, SettingsDto::getNotesPagination);
            case LIST_LANGUAGES -> resolvePagination(cwa, SettingsDto::getLanguagesPagination);
            case LIST_TAGS -> resolvePagination(cwa, SettingsDto::getTagsPagination);
            case START_VOCABULARY_TRAINING_SESSION -> startVocabularyTrainingSession(cwa);
            case DOWNLOAD -> resolveFileName(cwa, propertyHolder::getDownloadFilename);
            case UPLOAD -> resolveFileName(cwa, propertyHolder::getUploadFilename);
            default -> cwa;
        };
    }

    private CommandWithArgs resolveFileName(CommandWithArgs cwa, Supplier<String> filenameSupplier) {
        return cwa.getFilename() == null
                ? cwa.toBuilder().filename(filenameSupplier.get()).build()
                : cwa;
    }

    private CommandWithArgs startVocabularyTrainingSession(CommandWithArgs cwa) {
        var mode = cwa.getMode() == null
                ? VocabularyTrainingSessionMode.getDefaultMode()
                : cwa.getMode();

        var sessionSize = cwa.getEntriesPerVocabularyTrainingSession() == null
                ? resolveVocabularyTrainingSessionSize()
                : cwa.getEntriesPerVocabularyTrainingSession();

        return cwa.toBuilder()
                .mode(mode)
                .entriesPerVocabularyTrainingSession(sessionSize)
                .build();
    }

    private Integer resolveVocabularyTrainingSessionSize() {
        return findSettingsIgnoringNotFound()
                .map(SettingsDto::getEntriesPerVocabularyTrainingSession)
                .orElse(propertyHolder.getEntriesPerVocabularyTrainingSession());
    }

    private CommandWithArgs resolvePagination(CommandWithArgs cwa, Function<SettingsDto, Integer> extractor) {
        return cwa.getPagination() == null
                ? cwa.toBuilder().pagination(findSettingsIgnoringNotFound()
                .map(extractor)
                .orElse(propertyHolder.getDefaultPagination())).build()
                : cwa;
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

}
