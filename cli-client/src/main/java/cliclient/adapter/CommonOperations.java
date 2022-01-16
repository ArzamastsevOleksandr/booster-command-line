package cliclient.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonOperations {

    private final CommandLineAdapter adapter;

    public void help() {
        adapter.writeLine("Type command or 'h' to get help.");
    }

}
