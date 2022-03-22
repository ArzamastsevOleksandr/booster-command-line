package api.vocabulary;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@FeignClient(value = "language", url = "http://localhost:8082/languages")
public interface LanguageApi {

    @GetMapping(value = "/")
    @ResponseStatus(OK)
    List<String> availableLanguages();

    @GetMapping(value = "/my")
    @ResponseStatus(OK)
    List<String> myLanguages();

    // todo: change mapping
    @GetMapping(value = "/name/{language}")
    @ResponseStatus(OK)
    String findByLanguageName(@PathVariable("language") String language);

}
