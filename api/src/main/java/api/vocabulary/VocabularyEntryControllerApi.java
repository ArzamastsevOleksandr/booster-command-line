package api.vocabulary;

import org.springframework.web.bind.annotation.*;

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

    @GetMapping(value = "/{id}")
    @ResponseStatus(OK)
    VocabularyEntryDto findById(@PathVariable("id") Long id);

}
