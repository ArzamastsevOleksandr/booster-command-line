package cliclient.postprocessor;

import api.settings.SettingsDto;
import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.command.arguments.VocabularyTrainingSessionMode;
import cliclient.config.PropertyHolder;
import cliclient.feign.settings.SettingsServiceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class CommandWithArgsPostProcessor {

    private final SettingsServiceClient settingsServiceClient;
    private final PropertyHolder propertyHolder;

    private static final Set<Command> PAGEABLE_COMMANDS = Set.of(
            Command.LIST_VOCABULARY_ENTRIES,
            Command.LIST_LANGUAGES,
            Command.LIST_NOTES,
            Command.LIST_TAGS
    );

    public CommandWithArgs process(CommandWithArgs cwa) {
        Command command = cwa.getCommand();
        if (isPageableCommand(command)) {
            Integer pagination = cwa.getPagination();
            if (pagination == null) {
                cwa = switch (command) {
                    case LIST_VOCABULARY_ENTRIES -> resolvePagination(cwa, SettingsDto::getVocabularyPagination);
                    case LIST_NOTES -> resolvePagination(cwa, SettingsDto::getNotesPagination);
                    case LIST_LANGUAGES -> resolvePagination(cwa, SettingsDto::getLanguagesPagination);
                    case LIST_TAGS -> resolvePagination(cwa, SettingsDto::getTagsPagination);
                    default -> cwa;
                };
            }
        } else if (command == Command.START_VOCABULARY_TRAINING_SESSION) {
            if (cwa.getMode() == null) {
                cwa = cwa.toBuilder().mode(VocabularyTrainingSessionMode.getDefaultMode()).build();
            }
        } else if (command == Command.DOWNLOAD) {
            if (cwa.getFilename() == null) {
                cwa = cwa.toBuilder().filename(propertyHolder.getDownloadFilename()).build();
            }
        } else if (command == Command.UPLOAD) {
            if (cwa.getFilename() == null) {
                cwa = cwa.toBuilder().filename(propertyHolder.getUploadFilename()).build();
            }
        }
        return cwa;
    }

    private CommandWithArgs resolvePagination(CommandWithArgs commandWithArgs, Function<SettingsDto, Integer> extractor) {
        Integer pagination = findSettingsIgnoringNotFound()
                .map(extractor)
                .orElse(propertyHolder.getDefaultPagination());
        return commandWithArgs.toBuilder().pagination(pagination).build();
    }

    private Optional<SettingsDto> findSettingsIgnoringNotFound() {
        try {
            return Optional.of(settingsServiceClient.findOne());
        } catch (FeignException.FeignClientException ex) {
            if (ex.status() == HttpStatus.NOT_FOUND.value()) {
                return Optional.empty();
            }
            throw ex;
        }
    }

    private boolean isPageableCommand(Command command) {
        return PAGEABLE_COMMANDS.contains(command);
    }

}
