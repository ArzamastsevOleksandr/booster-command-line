package com.booster.dao.params;

import lombok.Value;

@Value
public class AddTagToNoteDaoParams {

    String tag;
    long noteId;

}
