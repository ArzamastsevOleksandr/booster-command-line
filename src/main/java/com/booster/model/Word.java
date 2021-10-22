package com.booster.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Word {

    long id;
    String name;

}
