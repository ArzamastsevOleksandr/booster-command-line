package tagservice

import api.tags.TagDto
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
@Transactional
class TagService {

    @Autowired
    lateinit var tagRepository: TagRepository

    fun findAll(): MutableCollection<TagDto> {
        return tagRepository.findAll()
            .map { toDto(it) }
            .toMutableList()
    }

    private fun toDto(tagEntity: TagEntity): TagDto {
        return TagDto.builder()
            .id(tagEntity.id)
            .name(tagEntity.name)
            .build()
    }

}
