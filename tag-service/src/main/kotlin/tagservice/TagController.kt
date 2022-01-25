package tagservice

import api.tags.TagDto
import api.tags.TagServiceApi
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
@RequestMapping("/tags/")
class TagController : TagServiceApi {

    @Autowired
    lateinit var tagService: TagService

    override fun findAll(): MutableCollection<TagDto> {
        return tagService.findAll()
    }

    override fun findById(id: Long): TagDto {
        return tagService.findById(id)
    }

}