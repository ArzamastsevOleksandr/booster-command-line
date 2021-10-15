package com.booster.model;

import lombok.Builder;
import lombok.Data;

// todo: immutable? remove @Data and leave only the necessary annotations?
@Data
@Builder
public class Word {

    private long id;
    private String name;

}
