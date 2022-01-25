package api.tags;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;

import static org.springframework.http.HttpStatus.OK;

public interface TagServiceApi {

    @GetMapping(value = "/")
    @ResponseStatus(OK)
    Collection<TagDto> findAll();

    @GetMapping(value = "/{id}")
    @ResponseStatus(OK)
    TagDto findById(@PathVariable("id") Long id);

}
