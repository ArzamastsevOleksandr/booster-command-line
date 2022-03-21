package api.upload;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@FeignClient(value = "download", url = "http://localhost:8083/download/")
public interface DownloadApi {

    @GetMapping(value = "/", produces = APPLICATION_OCTET_STREAM_VALUE)
    @ResponseStatus(OK)
    byte[] download();

}
