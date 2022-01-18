package api.vocabulary;

import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public interface LanguageControllerApi {

    @GetMapping(value = "/")
    @ResponseStatus(OK)
    Collection<LanguageDto> getAll();

    @GetMapping(value = "/{id}")
    @ResponseStatus(OK)
    LanguageDto findById(@PathVariable("id") Long id);

    @PostMapping(value = "/")
    @ResponseStatus(CREATED)
    LanguageDto add(@RequestBody AddLanguageInput input);

}
