package api.tags;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;

public interface TagServiceApi {

    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    Collection<TagDto> findAll();

}
