package vocabularyservice.language;

import api.vocabulary.AddLanguageInput;
import api.vocabulary.LanguageApi;
import api.vocabulary.LanguageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/languages/")
class LanguageController implements LanguageApi {

    final LanguageService languageService;

    @Override
    public Collection<LanguageDto> getAll() {
        return languageService.getAll();
    }

    @Override
    public LanguageDto findById(Long id) {
        return languageService.findById(id);
    }

    @Override
    public LanguageDto findByName(String name) {
        return languageService.findByName(name);
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
