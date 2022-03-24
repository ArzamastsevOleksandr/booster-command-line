package vocabularyservice.vocabularyentry;

import api.vocabulary.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vocabulary-entries/")
class VocabularyEntryController implements VocabularyEntryApi {

    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public List<VocabularyEntryDto> findFirst(Integer limit) {
        return vocabularyEntryService.findFirst(limit);
    }

    @Override
    public List<VocabularyEntryDto> findFirstWithSubstring(Integer limit, String substring) {
        return vocabularyEntryService.findFirstWithSubstring(limit, substring);
    }

    @Override
    public Collection<VocabularyEntryDto> findWithSynonyms(Integer limit) {
        return vocabularyEntryService.findWithSynonyms(limit);
    }

    @Override
    public List<VocabularyEntryDto> findAllByLanguage(String language) {
        return vocabularyEntryService.findAllByLanguage(language);
    }

    @Override
    public VocabularyEntryDto create(AddVocabularyEntryInput input) {
        return vocabularyEntryService.create(input);
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

    @Override
    public List<VocabularyEntryDto> patchLastSeenAt(PatchVocabularyEntryLastSeenAtInput input) {
        return vocabularyEntryService.patchLastSeenAt(input);
    }

    @Override
    public Integer countAll() {
        return vocabularyEntryService.countAll();
    }

    @Override
    public Integer countWithSubstring(String substring) {
        return vocabularyEntryService.countWithSubstring(substring);
    }

}
