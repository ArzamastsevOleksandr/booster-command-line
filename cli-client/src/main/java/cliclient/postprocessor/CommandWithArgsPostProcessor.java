package cliclient.postprocessor;

import api.settings.SettingsDto;
import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
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

    public CommandWithArgs process(CommandWithArgs commandWithArgs) {
        Command command = commandWithArgs.getCommand();
        if (isPageableCommand(command)) {
            Integer pagination = commandWithArgs.getPagination();
            if (pagination == null) {
                commandWithArgs = switch (command) {
                    case LIST_VOCABULARY_ENTRIES -> resolvePagination(commandWithArgs, SettingsDto::getVocabularyPagination);
                    case LIST_NOTES -> resolvePagination(commandWithArgs, SettingsDto::getNotesPagination);
                    case LIST_LANGUAGES -> resolvePagination(commandWithArgs, SettingsDto::getLanguagesPagination);
                    case LIST_TAGS -> resolvePagination(commandWithArgs, SettingsDto::getTagsPagination);
                    default -> commandWithArgs;
                };
            }
        }
        return commandWithArgs;
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
