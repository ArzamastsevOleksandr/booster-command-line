package cliclient.service;

import cliclient.dao.WordDao;
import cliclient.model.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordDao wordDao;

    public Word findByNameOrCreateAndGet(String name) {
        if (existsWithName(name)) {
            return wordDao.findByName(name);
        }
        long id = wordDao.createWithName(name);
        return Word.builder().id(id).name(name).build();
    }

    private boolean existsWithName(String name) {
        return wordDao.countWithName(name) == 1;
    }

    public List<Word> findAll() {
        return wordDao.findAll();
    }

}
