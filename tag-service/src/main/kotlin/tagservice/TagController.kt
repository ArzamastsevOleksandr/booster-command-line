package tagservice

import api.tags.CreateTagInput
import api.tags.TagDto
import api.tags.TagsApi
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
@RequestMapping("/tags/")
class TagController : TagsApi {

    @Autowired
    lateinit var tagService: TagService

    override fun findAll(): MutableCollection<TagDto> {
        return tagService.findAll()
    }

    override fun findByName(name: String): TagDto {
        return tagService.findByName(name)
    }

    override fun findById(id: Long): TagDto {
        return tagService.findById(id)
    }

    override fun create(input: CreateTagInput): TagDto {
        return tagService.create(input)
    }

}