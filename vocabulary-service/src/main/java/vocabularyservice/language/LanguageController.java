package vocabularyservice.language;

import api.vocabulary.LanguageControllerApi;
import api.vocabulary.LanguageDto;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
record LanguageController(LanguageService languageService) implements LanguageControllerApi {

    @Override
    public Collection<LanguageDto> getAll() {
        return languageService.getAll();
    }

}
