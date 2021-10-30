package com.booster.service;

import com.booster.dao.SettingsDao;
import com.booster.model.Settings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SettingsDao settingsDao;

    public Optional<Settings> findOne() {
        if (settingsDao.count() == 1) {
            return Optional.of(settingsDao.findOne());
        }
        return Optional.empty();
    }

    public boolean existAny() {
        return settingsDao.count() > 0;
    }

}
