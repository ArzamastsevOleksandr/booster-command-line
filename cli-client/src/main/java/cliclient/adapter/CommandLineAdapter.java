package cliclient.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class CommandLineAdapter {

    private final BufferedReader bufferedReader;

    public CommandLineAdapter(BufferedReader bufferedReader) {
        this.bufferedReader = Objects.requireNonNullElseGet(bufferedReader,
                () -> new BufferedReader(new InputStreamReader(System.in))
        );
    }

    public String readLine() {
        try {
            return bufferedReader.readLine().strip();
        } catch (IOException e) {
            e.printStackTrace();
            return "AN ERROR OCCURRED";
        }
    }

    public void writeLine(Object obj) {
        System.out.println(obj);
    }

    public void write(Object obj) {
        System.out.print(obj);
    }

    public void newLine() {
        System.out.println();
    }

}
