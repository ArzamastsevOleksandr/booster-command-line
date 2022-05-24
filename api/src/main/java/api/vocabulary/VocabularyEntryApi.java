package api.vocabulary;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@FeignClient(value = "vocabulary-entry", url = "http://localhost:8081/vocabulary-entries")
public interface VocabularyEntryApi {

    @GetMapping(value = "/", params = {"limit"})
    @ResponseStatus(OK)
    List<VocabularyEntryDto> findFirst(@RequestParam(name = "limit") Integer limit);

    @GetMapping(value = "/with-substring/{substring}", params = {"limit"})
    @ResponseStatus(OK)
    List<VocabularyEntryDto> findFirstWithSubstring(@RequestParam(name = "limit") Integer limit,
                                                    @PathVariable("substring") String substring);

    @GetMapping(value = "/with-synonyms/", params = {"limit"})
    List<VocabularyEntryDto> findWithSynonyms(@RequestParam("limit") Integer limit);

    @GetMapping(value = "/with-translations/", params = {"limit"})
    List<VocabularyEntryDto> findWithTranslations(@RequestParam("limit") Integer limit);

    // todo: pagination
    @GetMapping(value = "/language/{language}")
    @ResponseStatus(OK)
    List<VocabularyEntryDto> findAllByLanguage(@PathVariable("language") String language);

    @PostMapping(value = "/")
    @ResponseStatus(CREATED)
    VocabularyEntryDto create(@RequestBody AddVocabularyEntryInput input);

    @GetMapping(value = "/{id}")
    @ResponseStatus(OK)
    VocabularyEntryDto findById(@PathVariable("id") Long id);

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable("id") Long id);

    @Deprecated
    @PatchMapping(value = "/")
    @ResponseStatus(OK)
    VocabularyEntryDto patchEntry(@RequestBody PatchVocabularyEntryInput input);

    @PatchMapping(value = "/patch/last-seen-at/")
    @ResponseStatus(OK)
    List<VocabularyEntryDto> patchLastSeenAt(@RequestBody PatchVocabularyEntryLastSeenAtInput input);

    @GetMapping(value = "/count-all/")
    @ResponseStatus(OK)
    Integer countAll();

    @GetMapping(value = "/count-with-substring/{substring}")
    @ResponseStatus(OK)
    Integer countWithSubstring(@PathVariable("substring") String substring);

}
