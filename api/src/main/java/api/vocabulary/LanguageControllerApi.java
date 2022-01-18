package api.vocabulary;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;

@RequestMapping("/languages/")
public interface LanguageControllerApi {

    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    Collection<LanguageDto> getAll();

}
