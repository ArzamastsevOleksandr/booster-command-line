package api.vocabulary;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static org.springframework.http.HttpStatus.*;

@FeignClient(value = "language", url = "http://localhost:8082/languages")
public interface LanguageApi {

    @GetMapping(value = "/")
    @ResponseStatus(OK)
    Collection<LanguageDto> getAll();

    @GetMapping(value = "/{id}")
    @ResponseStatus(OK)
    LanguageDto findById(@PathVariable("id") Long id);

    @GetMapping(value = "/name/{name}")
    @ResponseStatus(OK)
    LanguageDto findByName(@PathVariable("name") String name);

    @PostMapping(value = "/")
    @ResponseStatus(CREATED)
    LanguageDto add(@RequestBody AddLanguageInput input);

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(NO_CONTENT)
    void deleteById(@PathVariable("id") Long id);

}
