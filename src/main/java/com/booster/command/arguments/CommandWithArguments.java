package com.booster.command.arguments;

import com.booster.command.Command;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommandWithArguments {

    private Command command;
    private List<String> arguments;

}
