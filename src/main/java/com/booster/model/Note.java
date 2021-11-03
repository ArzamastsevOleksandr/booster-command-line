package com.booster.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Note {

    long id;
    String content;

}
