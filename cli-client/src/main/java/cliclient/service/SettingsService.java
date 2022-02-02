package cliclient.service;

import api.settings.SettingsDto;
import cliclient.feign.settings.SettingsServiceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SettingsServiceClient settingsServiceClient;

    public Optional<SettingsDto> findOne() {
        try {
            return Optional.ofNullable(settingsServiceClient.findOne());
        } catch (FeignException.FeignClientException e) {
            if (e.status() == HttpStatus.NOT_FOUND.value()) {
                return Optional.empty();
            }
            throw e;
        }
    }

}
