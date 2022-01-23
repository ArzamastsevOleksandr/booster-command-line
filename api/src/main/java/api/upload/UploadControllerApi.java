package api.upload;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.ACCEPTED;

public interface UploadControllerApi {

    @PostMapping(value = "/")
    @ResponseStatus(ACCEPTED)
    UploadResponse upload(@RequestParam("file") MultipartFile file);

}
