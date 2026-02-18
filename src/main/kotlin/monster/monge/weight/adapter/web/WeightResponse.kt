package monster.monge.weight.adapter.web

import monster.monge.weight.domain.Weight
import monster.monge.weight.domain.WeightStat
import java.math.BigDecimal
import java.time.LocalDate

data class WeightResponse(
    val id: Long,
    val value: BigDecimal,
    val recordedAt: LocalDate,
    val memo: String?,
) {
    companion object {
        fun from(weight: Weight) = WeightResponse(
            id = weight.id!!,
            value = weight.value,
            recordedAt = weight.recordedAt,
            memo = weight.memo,
        )
    }
}

data class WeightStatResponse(
    val max: BigDecimal,
    val min: BigDecimal,
    val average: BigDecimal,
    val change: BigDecimal,
) {
    companion object {
        fun from(stat: WeightStat) = WeightStatResponse(
            max = stat.max,
            min = stat.min,
            average = stat.average,
            change = stat.change,
        )
    }
}
