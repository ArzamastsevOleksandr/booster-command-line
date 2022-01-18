package vocabularyservice.language;

import api.vocabulary.AddLanguageInput;
import api.vocabulary.LanguageControllerApi;
import api.vocabulary.LanguageDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/languages/")
record LanguageController(LanguageService languageService) implements LanguageControllerApi {

    @Override
    public Collection<LanguageDto> getAll() {
        return languageService.getAll();
    }

    @Override
    public LanguageDto findById(Long id) {
        return languageService.findById(id);
    }

    @Override
    public LanguageDto add(AddLanguageInput input) {
        return languageService.add(input);
    }

    @Override
    public void deleteById(Long id) {
        languageService.deleteById(id);
    }

}
