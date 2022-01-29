package cliclient.command.handler;

import api.upload.UploadResponse;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.UploadCommandArgs;
import cliclient.feign.upload.UploadServiceClient;
import lombok.RequiredArgsConstructor;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class UploadCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final UploadServiceClient uploadServiceClient;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (UploadCommandArgs) commandArgs;
        boolean fileExists = ofNullable(args.filename())
                .map(this::fileExists)
                .orElse(false);

        if (fileExists) {
            doUpload(args.filename());
        } else {
            adapter.writeLine("File " + args.filename() + " or default upload file do not exist");
        }
    }

    private void doUpload(String filename) {
        try {
            MultipartFile multipartFile = createMultipartFile(filename);
            UploadResponse uploadResponse = uploadServiceClient.upload(multipartFile);

            adapter.writeLine("Notes uploaded: " + uploadResponse.notesUploaded());
            adapter.writeLine("Vocabulary entries uploaded: " + uploadResponse.vocabularyEntriesUploaded());
        } catch (IOException e) {
            adapter.writeLine("Error during upload process: " + e.getMessage());
        }
    }

    private boolean fileExists(String filename) {
        var file = new File(filename);
        return file.exists() && !file.isDirectory();
    }

    private MultipartFile createMultipartFile(String filename) throws IOException {
        var file = new File(filename);
        FileItem fileItem = new DiskFileItemFactory()
                .createItem("file", Files.probeContentType(file.toPath()), false, file.getName());

        try (var in = new FileInputStream(file);
             var out = fileItem.getOutputStream()) {
            in.transferTo(out);
        }
        return new CommonsMultipartFile(fileItem);
    }

    @Override
    public Command getCommand() {
        return Command.UPLOAD;
    }

}
