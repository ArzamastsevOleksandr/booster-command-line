package api.tags;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TagDto {

    Long id;
    String name;

}
