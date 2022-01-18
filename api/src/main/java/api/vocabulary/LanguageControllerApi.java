package api.vocabulary;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;

import static org.springframework.http.HttpStatus.OK;

public interface LanguageControllerApi {

    @GetMapping(value = "/")
    @ResponseStatus(OK)
    Collection<LanguageDto> getAll();

    @GetMapping(value = "/{id}")
    @ResponseStatus(OK)
    LanguageDto findById(@PathVariable("id") Long id);

}
