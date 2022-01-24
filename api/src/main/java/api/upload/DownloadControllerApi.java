package api.upload;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

public interface DownloadControllerApi {

    @GetMapping(value = "/", produces = APPLICATION_OCTET_STREAM_VALUE)
    @ResponseStatus(OK)
    byte[] download();

}
