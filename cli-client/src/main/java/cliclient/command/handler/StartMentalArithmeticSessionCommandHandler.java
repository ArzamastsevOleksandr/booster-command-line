package cliclient.command.handler;

import api.math.ArithmeticExpression;
import api.math.MathApi;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.args.CmdArgs;
import cliclient.command.args.StartMentalArithmeticSessionCmdWithArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartMentalArithmeticSessionCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final MathApi mathApi;

    @Override
    public void handle(CmdArgs cwa) {
        ArithmeticExpression expression = getArithmeticExpression();
        String answer = readAnswer();

        while (!answer.equals("e")) {
            int answerInt = Integer.parseInt(answer);
            int actual = expression.getA() * expression.getB();

            if (actual == answerInt) {
                adapter.greenLine("Good");
            } else {
                adapter.error("Answer is: " + actual);
            }

            expression = getArithmeticExpression();
            answer = readAnswer();
        }
        adapter.writeLine("Mental arithmetic session finished");
    }

    private String readAnswer() {
        adapter.write(" >> ");
        return adapter.readLine();
    }

    private ArithmeticExpression getArithmeticExpression() {
        ArithmeticExpression expression = mathApi.random(1);
        adapter.writeLine(expression);
        return expression;
    }

    @Override
    public Class<? extends CmdArgs> getCmdArgsClass() {
        return StartMentalArithmeticSessionCmdWithArgs.class;
    }

}
