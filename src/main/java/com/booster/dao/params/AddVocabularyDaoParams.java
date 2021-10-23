package com.booster.dao.params;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AddVocabularyDaoParams {

    String name;
    long languageBeingLearnedId;

}
