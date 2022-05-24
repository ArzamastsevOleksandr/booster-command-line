package booster.vocabulary.language;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LanguageService {

    public List<String> getAllAvailable() {
        return Arrays.stream(Language.values())
                .map(Language::getName)
                .toList();
    }

    public String findByLanguageName(String language) {
        return Language.fromString(language).getName();
    }

}
