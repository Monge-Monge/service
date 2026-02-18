package monster.monge.weight.domain

import java.math.BigDecimal

data class WeightStat(
    val max: BigDecimal,
    val min: BigDecimal,
    val average: BigDecimal,
    val change: BigDecimal,
)
