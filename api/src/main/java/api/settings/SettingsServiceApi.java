package api.settings;

import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

public interface SettingsServiceApi {

    @GetMapping(value = "/")
    @ResponseStatus(OK)
    SettingsDto findOne();

    @PostMapping(value = "/")
    @ResponseStatus(CREATED)
    SettingsDto create(@RequestBody CreateSettingsInput input);

    @DeleteMapping(value = "/")
    @ResponseStatus(NO_CONTENT)
    void delete();

    @PatchMapping(value = "/")
    @ResponseStatus(OK)
    SettingsDto patch(@RequestBody PatchSettingsInput input);

}
