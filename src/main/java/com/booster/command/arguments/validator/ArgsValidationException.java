package com.booster.command.arguments.validator;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value
@RequiredArgsConstructor
public class ArgsValidationException extends RuntimeException {

    // todo: constructor with var args
    List<String> argErrors;

    public List<String> getArgErrors() {
        return Collections.unmodifiableList(argErrors);
    }

}
