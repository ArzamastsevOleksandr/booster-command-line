package com.booster.output;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandLineWriter {

    public void writeLine(String str) {
        System.out.println(str);
    }

    public void write(String str) {
        System.out.print(str);
    }

}
