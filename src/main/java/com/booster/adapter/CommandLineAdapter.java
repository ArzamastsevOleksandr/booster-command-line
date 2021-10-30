package com.booster.adapter;

import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;

@RequiredArgsConstructor
public class CommandLineAdapter {

    private final BufferedReader bufferedReader;

    public String readLine() {
        try {
            return bufferedReader.readLine().strip();
        } catch (IOException e) {
            e.printStackTrace();
            return "AN ERROR OCCURRED";
        }
    }

    public void writeLine(String str) {
        System.out.println(str);
    }

    public void write(String str) {
        System.out.print(str);
    }

    public void newLine() {
        System.out.println();
    }

}
