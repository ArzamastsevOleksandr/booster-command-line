package com.booster.service;

import com.booster.dao.TagDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagDao tagDao;

    public boolean existsWithName(String tag) {
        var count = tagDao.countWithName(tag);
        return count == 1;
    }

}
