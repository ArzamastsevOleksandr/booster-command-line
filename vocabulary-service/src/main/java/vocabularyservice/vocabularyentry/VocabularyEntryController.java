package vocabularyservice.vocabularyentry;

import api.vocabulary.VocabularyEntryControllerApi;
import api.vocabulary.VocabularyEntryDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/vocabulary-entries/")
record VocabularyEntryController(VocabularyEntryService vocabularyEntryService) implements VocabularyEntryControllerApi {

    @Override
    public Collection<VocabularyEntryDto> getAll() {
        return vocabularyEntryService.findAll();
    }

}
