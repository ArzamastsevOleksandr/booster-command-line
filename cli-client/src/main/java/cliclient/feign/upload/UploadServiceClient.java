package cliclient.feign.upload;

import api.upload.UploadApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "upload-service-client", url = "http://localhost:8083/upload/", configuration = FeignSupportConfig.class)
public interface UploadServiceClient extends UploadApi {
}
