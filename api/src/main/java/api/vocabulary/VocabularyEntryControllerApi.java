package api.vocabulary;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public interface VocabularyEntryControllerApi {

    @GetMapping(value = "/", params = {"limit"})
    @ResponseStatus(OK)
    List<VocabularyEntryDto> findFirst(@RequestParam(name = "limit") Integer limit);

    @GetMapping(value = "/with-synonyms/", params = {"limit"})
    Collection<VocabularyEntryDto> findWithSynonyms(@RequestParam("limit") Integer limit);

    @GetMapping(value = "/languageId/{id}")
    @ResponseStatus(OK)
    Collection<VocabularyEntryDto> findAllByLanguageId(@PathVariable("id") Long id);

    @PostMapping(value = "/")
    @ResponseStatus(CREATED)
    VocabularyEntryDto add(@RequestBody AddVocabularyEntryInput input);

    @GetMapping(value = "/{id}")
    @ResponseStatus(OK)
    VocabularyEntryDto findById(@PathVariable("id") Long id);

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable("id") Long id);

    @PatchMapping(value = "/")
    @ResponseStatus(OK)
    VocabularyEntryDto patchEntry(@RequestBody PatchVocabularyEntryInput input);

    @GetMapping(value = "/count-all/")
    @ResponseStatus(OK)
    Integer countAll();

    @GetMapping(value = "/count-with-substring/{substring}")
    @ResponseStatus(OK)
    Integer countWithSubstring(@PathVariable("substring") String substring);

}
