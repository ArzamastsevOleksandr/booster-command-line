package booster.math

import api.math.ArithmeticExpression
import api.math.MathApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mental-arithmetic")
class MathController : MathApi {

    @Autowired
    lateinit var expressionService: ExpressionService

    override fun random(level: Int): ArithmeticExpression {
        return expressionService.generate(level)
    }

}