package cliclient.feign.vocabulary;

import api.vocabulary.LanguageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@FeignClient(value = "language-controller-api-client", url = "http://localhost:8082/languages")
public interface LanguageControllerApiClient {

    @GetMapping(value = "/")
    Collection<LanguageDto> getAll();

}
