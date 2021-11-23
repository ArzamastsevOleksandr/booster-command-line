package booster.service;

import booster.dao.LanguageDao;
import booster.model.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageDao languageDao;

    public Optional<Language> findByName(String name) {
        if (languageDao.countWithName(name) == 1) {
            return Optional.of(languageDao.findByName(name));
        }
        return Optional.empty();
    }

    public boolean existsWithId(long id) {
        return languageDao.countWithId(id) == 1;
    }

    public boolean existsWithName(String name) {
        return languageDao.countWithName(name) == 1;
    }

    public Language add(String name) {
        long id = languageDao.add(name);
        return languageDao.findById(id);
    }

    public void delete(Long id) {
        languageDao.delete(id);
    }

    public List<Language> findAll() {
        return languageDao.findAll();
    }

}
