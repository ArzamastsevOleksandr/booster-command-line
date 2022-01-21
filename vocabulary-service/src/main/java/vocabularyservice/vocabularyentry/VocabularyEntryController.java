package vocabularyservice.vocabularyentry;

import api.vocabulary.AddVocabularyEntryInput;
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
    public VocabularyEntryDto add(AddVocabularyEntryInput input) {
        return vocabularyEntryService.add(input);
    }

}
