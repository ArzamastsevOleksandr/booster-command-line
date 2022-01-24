package cliclient.feign.upload;

import api.upload.DownloadControllerApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "download-service-client", url = "http://localhost:8083/download/")
public interface DownloadServiceClient extends DownloadControllerApi {
}
