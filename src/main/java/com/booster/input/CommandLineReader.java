package com.booster.input;

import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;

@RequiredArgsConstructor
public class CommandLineReader {

    private final BufferedReader bufferedReader;

    public String readLine() {
        try {
            return bufferedReader.readLine().trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "AN ERROR OCCURRED";
        }
    }

}
