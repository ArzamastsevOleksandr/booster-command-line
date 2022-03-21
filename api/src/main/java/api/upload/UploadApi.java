package api.upload;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

// TODO: no children
public interface UploadApi {

    @PostMapping(value = "/", consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(ACCEPTED)
    UploadResponse upload(@RequestPart(value = "file") MultipartFile file);

}
