package api.vocabulary;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;

import static org.springframework.http.HttpStatus.OK;

public interface VocabularyEntryControllerApi {

    @GetMapping(value = "/")
    @ResponseStatus(OK)
    Collection<VocabularyEntryDto> getAll();

}
