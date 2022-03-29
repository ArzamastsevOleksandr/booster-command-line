package cliclient.command.handler;

import api.upload.DownloadApi;
import cliclient.command.Command;
import cliclient.command.args.CmdArgs;
import cliclient.command.args.DownloadCmdArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DownloadCommandHandler implements CommandHandler {

    private final DownloadApi downloadApi;

    // todo: if the download file already exists - warn and ask for confirmation
    @Override
    public void handle(CmdArgs cwa) {
        var args = (DownloadCmdArgs) cwa;
        byte[] bytes = downloadApi.download();
        // todo: settings + properties
        try (var out = new FileOutputStream(args.filename())) {
            out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command getCommand() {
        return Command.DOWNLOAD;
    }

}
