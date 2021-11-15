package com.booster.dao.params;

import lombok.Value;

@Value
public class AddTagToVocabularyEntryDaoParams {

    String tag;
    long vocabularyEntryId;

}
