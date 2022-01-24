package api.settings;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface SettingsServiceApi {

    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    SettingsDto findOne();

}
