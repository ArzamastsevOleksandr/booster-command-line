package api.settings;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public interface SettingsServiceApi {

    @GetMapping(value = "/")
    @ResponseStatus(OK)
    SettingsDto findOne();

    @PostMapping(value = "/")
    @ResponseStatus(CREATED)
    SettingsDto create(@RequestBody CreateSettingsInput input);

}
