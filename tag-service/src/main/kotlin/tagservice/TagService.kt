package tagservice

import api.exception.NotFoundException
import api.tags.CreateTagInput
import api.tags.TagDto
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalStateException

@Slf4j
@Service
@Transactional(readOnly = true)
class TagService {

    @Autowired
    lateinit var tagRepository: TagRepository

    fun findAll(): MutableList<TagDto> {
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

    fun findById(id: Long): TagDto {
        return tagRepository.findById(id)
            .map { toDto(it) }
            .orElseThrow { NotFoundException("Tag not found by id: $id") }
    }

    @Transactional
    fun create(input: CreateTagInput): TagDto {
        tagRepository.findByName(input.name)
            ?.let { throw IllegalStateException("Tag already exists with name: ${input.name}") }

        val tagEntity = TagEntity()
        tagEntity.name = input.name
        return toDto(tagRepository.save(tagEntity))
    }

    fun findByName(name: String): TagDto {
        val tagEntity = tagRepository.findByName(name) ?: throw NotFoundException("Tag not found by name: $name")
        return toDto(tagEntity)
    }

    fun findByNames(names: MutableSet<String>): MutableList<TagDto> {
        val tagEntities = tagRepository.findByNames(names)
        val foundTagNames = tagEntities.map { tagEntity -> tagEntity.name }.toSet()

        checkThatAllTagsFound(foundTagNames, names)

        return tagEntities.map { tagEntity ->  toDto(tagEntity) }.toMutableList()
    }

    private fun checkThatAllTagsFound(foundTagNames: Set<String>, names: MutableSet<String>) {
        if (foundTagNames != names) {
            val namesCopy = HashSet<String>(names)
            namesCopy.removeAll(foundTagNames)
            throw NotFoundException("Tags not found: $namesCopy")
        }
    }

}
