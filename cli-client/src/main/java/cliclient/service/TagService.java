package cliclient.service;

import cliclient.dao.TagDao;
import cliclient.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagDao tagDao;
    private final TransactionTemplate transactionTemplate;

    public boolean existsWithName(String tag) {
        var count = tagDao.countWithName(tag);
        return count == 1;
    }

    // todo: for import process: custom cache to avoid db round trips
    public void createIfNotExist(Set<String> tags) {
        transactionTemplate.executeWithoutResult(status -> tagDao.addAll(new ArrayList<>(tags)));
    }

    public Tag add(String name) {
        String tag = tagDao.add(name);
        return tagDao.findByName(tag);
    }

    public List<Tag> findAll() {
        return tagDao.findAll();
    }

}
