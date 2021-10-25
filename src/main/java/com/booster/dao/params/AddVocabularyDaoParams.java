package com.booster.dao.params;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AddVocabularyDaoParams {

    // todo: extract to configurable setting?
    @Builder.Default
    String name = "DEFAULT";

    long languageBeingLearnedId;

}
