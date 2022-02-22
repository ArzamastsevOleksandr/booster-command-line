package vocabularyservice.feign;

import api.settings.SettingsServiceApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "settings-service-client", url = "http://localhost:8084/settings/")
public interface SettingsServiceClient extends SettingsServiceApi {
}
