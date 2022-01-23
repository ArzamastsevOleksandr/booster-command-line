package uploadservice.feign.vocabulary;

import api.vocabulary.LanguageControllerApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "language-controller-api-client", url = "http://localhost:8082/languages")
public interface LanguageControllerApiClient extends LanguageControllerApi {
}
