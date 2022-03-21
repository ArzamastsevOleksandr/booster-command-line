package cliclient.service;

import api.settings.SettingsApi;
import api.settings.SettingsDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SettingsApi settingsApi;

    public Optional<SettingsDto> findOne() {
        try {
            return Optional.ofNullable(settingsApi.findOne());
        } catch (FeignException.FeignClientException e) {
            if (e.status() == HttpStatus.NOT_FOUND.value()) {
                return Optional.empty();
            }
            throw e;
        }
    }

}
