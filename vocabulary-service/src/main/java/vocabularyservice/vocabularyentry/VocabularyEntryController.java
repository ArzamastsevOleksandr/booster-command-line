package vocabularyservice.vocabularyentry;

import api.vocabulary.AddVocabularyEntryInput;
import api.vocabulary.PatchVocabularyEntryInput;
import api.vocabulary.VocabularyEntryControllerApi;
import api.vocabulary.VocabularyEntryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vocabulary-entries/")
class VocabularyEntryController implements VocabularyEntryControllerApi {

    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public Collection<VocabularyEntryDto> getAll() {
        return vocabularyEntryService.findAll();
    }

    @Override
    public Collection<VocabularyEntryDto> findWithSynonyms(Integer limit) {
        return vocabularyEntryService.findWithSynonyms(limit);
    }

    @Override
    public Collection<VocabularyEntryDto> findAllByLanguageId(Long id) {
        return vocabularyEntryService.findAllByLanguageId(id);
    }

    @Override
    public VocabularyEntryDto add(AddVocabularyEntryInput input) {
        return vocabularyEntryService.add(input);
    }

    @Override
    public VocabularyEntryDto findById(Long id) {
        return vocabularyEntryService.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        vocabularyEntryService.deleteById(id);
    }

    @Override
    public VocabularyEntryDto patchEntry(PatchVocabularyEntryInput input) {
        return vocabularyEntryService.patch(input);
    }

}
