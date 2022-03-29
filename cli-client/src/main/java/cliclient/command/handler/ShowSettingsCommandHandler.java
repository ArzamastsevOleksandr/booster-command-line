package cliclient.command.handler;

import api.settings.SettingsApi;
import api.settings.SettingsDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.args.CmdArgs;
import cliclient.command.args.ShowSettingsCmdWithArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShowSettingsCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final SettingsApi settingsApi;

    @Override
    public void handle(CmdArgs cwa) {
        SettingsDto settingsDto = settingsApi.findOne();
        adapter.writeLine(settingsDto);
    }

    @Override
    public Class<? extends CmdArgs> getCmdArgsClass() {
        return ShowSettingsCmdWithArgs.class;
    }

}
