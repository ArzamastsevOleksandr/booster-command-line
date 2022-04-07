package api.arithmetic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArithmeticExpression {
    String op;
    Integer a;
    Integer b;
    ArithmeticExpression exprA;
    ArithmeticExpression exprB;
}
