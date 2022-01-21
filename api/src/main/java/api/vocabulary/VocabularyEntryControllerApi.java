package api.vocabulary;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public interface VocabularyEntryControllerApi {

    @GetMapping(value = "/")
    @ResponseStatus(OK)
    Collection<VocabularyEntryDto> getAll();

    @PostMapping(value = "/")
    @ResponseStatus(CREATED)
    VocabularyEntryDto add(@RequestBody AddVocabularyEntryInput input);

}
