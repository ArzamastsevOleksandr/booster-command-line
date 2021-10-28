package com.booster.service;

import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class WrapperService {

    private final CommandLineWriter commandLineWriter;

    public <T> Optional<T> wrapDataAccessException(Supplier<T> s) {
        try {
            return Optional.ofNullable(s.get());
        } catch (DataAccessException e) {
            commandLineWriter.writeLine("An exception occurred: " + e.getMessage());
            return Optional.empty();
        }
    }

}
