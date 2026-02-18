package monster.monge.weight.adapter.web

import java.math.BigDecimal
import java.time.LocalDate

data class WeightCreateRequest(
    val value: BigDecimal,
    val recordedAt: LocalDate,
    val memo: String? = null,
)

data class WeightUpdateRequest(
    val value: BigDecimal,
    val recordedAt: LocalDate,
    val memo: String? = null,
)
