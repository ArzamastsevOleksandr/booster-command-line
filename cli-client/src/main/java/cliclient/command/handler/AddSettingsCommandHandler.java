package cliclient.command.handler;

import api.settings.CreateSettingsInput;
import api.settings.SettingsApi;
import api.settings.SettingsDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.args.AddSettingsCmdArgs;
import cliclient.command.args.CmdArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddSettingsCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final SettingsApi settingsApi;

    @Override
    public void handle(CmdArgs cwa) {
        var args = (AddSettingsCmdArgs) cwa;
        SettingsDto settingsDto = settingsApi.create(CreateSettingsInput.builder()

                .defaultLanguageName(args.getDefaultLanguageName())

                .entriesPerVocabularyTrainingSession(args.getEntriesPerVocabularyTrainingSession())

                .tagsPagination(args.getTagsPagination())
                .notesPagination(args.getNotesPagination())
                .vocabularyPagination(args.getVocabularyPagination())
                .languagesPagination(args.getLanguagesPagination())

                .build());
        adapter.writeLine(settingsDto);
    }

    @Override
    public Class<? extends CmdArgs> getCmdArgsClass() {
        return AddSettingsCmdArgs.class;
    }

}
