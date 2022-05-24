package booster.vocabulary.language;

import api.vocabulary.LanguageApi;
import booster.vocabulary.entry.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/languages/")
class LanguageController implements LanguageApi {

    final LanguageService languageService;
    final VocabularyEntryService vocabularyEntryService;

    @Override
    public List<String> availableLanguages() {
        return languageService.getAllAvailable();
    }

    @Override
    public List<String> myLanguages() {
        return vocabularyEntryService.myLanguages();
    }

    @Override
    public String findByLanguageName(String language) {
        return languageService.findByLanguageName(language);
    }

}
