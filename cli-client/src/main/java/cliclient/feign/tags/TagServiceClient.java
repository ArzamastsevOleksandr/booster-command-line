package cliclient.feign.tags;

import api.tags.TagServiceApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "tag-service-client", url = "http://localhost:8085/tags/")
public interface TagServiceClient extends TagServiceApi {
}
