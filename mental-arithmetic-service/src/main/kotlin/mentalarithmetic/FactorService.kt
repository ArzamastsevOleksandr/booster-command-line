package mentalarithmetic

import org.springframework.stereotype.Component
import java.util.concurrent.ThreadLocalRandom

@Component
class FactorService {

    private val minFactor: Int = 10
    private val factorStep: Int = 10
    private val rangeStep: Int = 5

    fun random(level: Int): Int {
        val rangeLevel = calculateRangeLevel(level)
        val upperBound = minFactor + factorStep * rangeLevel
        val lowerBound = upperBound - factorStep + 1
        return ThreadLocalRandom.current().nextInt(lowerBound, upperBound + 1)
    }

    private fun calculateRangeLevel(level: Int): Int {
        return level / rangeStep + 1
    }

}