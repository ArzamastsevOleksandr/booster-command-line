package com.booster.command.arguments.resolver;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value
@RequiredArgsConstructor
public class ArgsValidationException extends RuntimeException {

    List<String> argErrors;

}
