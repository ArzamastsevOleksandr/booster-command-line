package booster.dao.params;

import lombok.Value;

import java.util.Set;

@Value
public class AddTagsToNoteDaoParams {

    Set<String> tags;
    long noteId;

}
