package com.booster.service;

import com.booster.adapter.CommandLineAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class WrapperService {

    private final CommandLineAdapter adapter;

    public <T> Optional<T> wrapDataAccessException(Supplier<T> s) {
        try {
            return Optional.ofNullable(s.get());
        } catch (DataAccessException e) {
            adapter.writeLine("An exception occurred: " + e.getMessage());
            return Optional.empty();
        }
    }

}
