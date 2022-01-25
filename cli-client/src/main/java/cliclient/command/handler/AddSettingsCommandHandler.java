package cliclient.command.handler;

import api.settings.CreateSettingsInput;
import api.settings.SettingsDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.AddSettingsCommandArgs;
import cliclient.command.arguments.CommandArgs;
import cliclient.feign.settings.SettingsServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddSettingsCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final SettingsServiceClient settingsServiceClient;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (AddSettingsCommandArgs) commandArgs;
        SettingsDto settingsDto = settingsServiceClient.create(CreateSettingsInput.builder()
                .entriesPerVocabularyTrainingSession(args.entriesPerVocabularyTrainingSession())
                .defaultLanguageId(args.languageId())
                .build());
        adapter.writeLine(settingsDto);
    }

    @Override
    public Command getCommand() {
        return Command.ADD_SETTINGS;
    }

}
