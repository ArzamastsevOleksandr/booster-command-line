package api.tags;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@FeignClient(value = "tags", url = "http://localhost:8081/tags/")
public interface TagsApi {

    @GetMapping(value = "/")
    @ResponseStatus(OK)
    List<TagDto> findAll();

    @GetMapping(value = "/{id}")
    @ResponseStatus(OK)
    TagDto findById(@PathVariable("id") Long id);

    @GetMapping(value = "/name/{name}")
    @ResponseStatus(OK)
    TagDto findByName(@PathVariable("name") String name);

    @PostMapping(value = "/")
    @ResponseStatus(CREATED)
    TagDto create(@RequestBody CreateTagInput input);

    @PostMapping(value = "/find/name/batch")
    List<TagDto> findByNames(@RequestBody Set<String> names);

}
