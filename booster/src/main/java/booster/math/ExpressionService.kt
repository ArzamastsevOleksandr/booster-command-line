package booster.math

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ExpressionService {

    @Autowired
    lateinit var factorService: FactorService

    val groupSize: Int = 5

    fun generate(level: Int): api.math.ArithmeticExpression {
        val expressionType = when (level % groupSize) {
            1 -> ChallengeType.MULTIPLICATION
            2 -> ChallengeType.DIVISION
            3 -> ChallengeType.SUM_OR_SUBTRACTION_OF_MULTIPLICATIONS
            4 -> ChallengeType.SUM_OR_SUBTRACTION_OF_DIVISION_AND_MULTIPLICATION
            else -> ChallengeType.SUM_OR_SUBTRACTION_OF_DIVISIONS
        }
        return generate(level, expressionType)
    }

    private fun generate(level: Int, expressionType: ChallengeType): api.math.ArithmeticExpression {
        return when (expressionType) {
            ChallengeType.MULTIPLICATION -> multiplication(level)
            else -> multiplication(level)
        }
    }

    private fun multiplication(level: Int): api.math.ArithmeticExpression {
        return api.math.ArithmeticExpression.builder()
            .a(factorService.random(level))
            .b(factorService.random(level))
            .op("*")
            .build()
    }

}