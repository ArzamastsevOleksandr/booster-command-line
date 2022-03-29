package cliclient.command.handler;

import api.settings.SettingsApi;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.args.CmdArgs;
import cliclient.command.args.DeleteSettingsCmdArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteSettingsCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final SettingsApi settingsApi;

    @Override
    public void handle(CmdArgs cwa) {
        settingsApi.delete();
        adapter.writeLine("Done");
    }

    @Override
    public Class<? extends CmdArgs> getCmdArgsClass() {
        return DeleteSettingsCmdArgs.class;
    }

}
