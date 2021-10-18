package com.booster.command.arguments;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class AddLanguageBeingLearnedArgs implements Args {

    long languageId;

}
