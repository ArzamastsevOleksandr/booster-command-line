package mentalarithmetic

import api.arithmetic.ArithmeticExpression
import api.arithmetic.MentalArithmeticApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mental-arithmetic")
class MentalArithmeticController : MentalArithmeticApi {

    @Autowired
    lateinit var expressionService: ExpressionService

    override fun random(level: Int): ArithmeticExpression {
        return expressionService.generate(level)
    }

}