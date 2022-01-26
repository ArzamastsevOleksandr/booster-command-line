package api.tags;

import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public interface TagServiceApi {

    @GetMapping(value = "/")
    @ResponseStatus(OK)
    Collection<TagDto> findAll();

    @GetMapping(value = "/{id}")
    @ResponseStatus(OK)
    TagDto findById(@PathVariable("id") Long id);

    @GetMapping(value = "/name/{name}")
    @ResponseStatus(OK)
    TagDto findByName(@PathVariable("name") String name);

    @PostMapping(value = "/")
    @ResponseStatus(CREATED)
    TagDto create(@RequestBody CreateTagInput input);

}
