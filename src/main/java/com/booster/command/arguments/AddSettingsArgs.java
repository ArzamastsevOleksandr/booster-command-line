package com.booster.command.arguments;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AddSettingsArgs implements Args {

    Long languageBeingLearnedId;
    Long vocabularyId;

}
