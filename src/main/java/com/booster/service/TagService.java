package com.booster.service;

import com.booster.dao.TagDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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

    // todo: batch insert
    public void createIfNotExist(Set<String> tags) {
        transactionTemplate.executeWithoutResult(status -> tags.stream().filter(t -> !existsWithName(t)).forEach(tagDao::add));
    }

}
