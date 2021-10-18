package com.booster.command.arguments.resolver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

// todo: value class?
@Getter
@RequiredArgsConstructor
public class ArgsValidationException extends RuntimeException {

    private final List<String> argErrors;

}
