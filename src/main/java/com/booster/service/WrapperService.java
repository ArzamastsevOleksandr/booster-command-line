package com.booster.service;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

@Service
public class WrapperService<T> {

    public Optional<T> wrapDataAccessException(Supplier<T> s) {
        try {
            return Optional.ofNullable(s.get());
        } catch (DataAccessException e) {
            // todo: handle exceptions
            return Optional.empty();
        }
    }

}
