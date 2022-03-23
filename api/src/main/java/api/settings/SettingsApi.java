package api.settings;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@FeignClient(value = "settings", url = "http://localhost:8084/settings/")
public interface SettingsApi {

    @GetMapping(value = "/")
    @ResponseStatus(OK)
    SettingsDto findOne();

    @PostMapping(value = "/")
    @ResponseStatus(CREATED)
    SettingsDto create(@RequestBody CreateSettingsInput input);

    @DeleteMapping(value = "/")
    @ResponseStatus(NO_CONTENT)
    void delete();

    @Deprecated
    @PatchMapping(value = "/")
    @ResponseStatus(OK)
    SettingsDto patch(@RequestBody PatchSettingsInput input);

}
