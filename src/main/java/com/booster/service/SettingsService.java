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
    private final WrapperService wrapperService;

    public Optional<Settings> findOne() {
        return wrapperService.wrapDataAccessException(settingsDao::findOne);
    }

}
