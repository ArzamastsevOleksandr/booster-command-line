package com.booster.parser;

import com.booster.command.Command;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CommandArguments {

    Command command;

    Long id;
    String name;
    String description;

}
