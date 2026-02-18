package monster.monge.weight.application

import monster.monge.weight.application.provided.WeightFinder
import monster.monge.weight.application.required.WeightRepository
import monster.monge.weight.domain.Weight
import monster.monge.weight.domain.WeightStat
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Service
class WeightQueryService(
    private val weightRepository: WeightRepository
) : WeightFinder {

    @Transactional(readOnly = true)
    override fun findAll(accountId: Long): List<Weight> =
        weightRepository.findByAccountIdOrderByRecordedAtDesc(accountId)

    @Transactional(readOnly = true)
    override fun findById(accountId: Long, id: Long): Weight {
        val weight = weightRepository.findByIdOrNull(id) ?: throw NoSuchElementException("Weight not found: $id")
        require(weight.accountId == accountId) { "Access denied" }
        return weight
    }

    @Transactional(readOnly = true)
    override fun graph(accountId: Long, period: String): List<Weight> {
        val now = LocalDate.now()
        val from = when (period.uppercase()) {
            "WEEK" -> now.minusWeeks(1)
            "MONTH" -> now.minusMonths(1)
            "YEAR" -> now.minusYears(1)
            else -> throw IllegalArgumentException("Invalid period: $period")
        }
        return weightRepository.findByAccountIdAndRecordedAtBetweenOrderByRecordedAtAsc(accountId, from, now)
    }

    @Transactional(readOnly = true)
    override fun stats(accountId: Long): WeightStat {
        val weights = weightRepository.findByAccountIdOrderByRecordedAtDesc(accountId)
        if (weights.isEmpty()) throw NoSuchElementException("No weight records found")
        val values = weights.map { it.value }
        val max = values.max()
        val min = values.min()
        val average = values.fold(BigDecimal.ZERO) { acc, v -> acc + v }
            .divide(BigDecimal(values.size), 2, RoundingMode.HALF_UP)
        val sorted = weights.sortedBy { it.recordedAt }
        val change = sorted.last().value - sorted.first().value
        return WeightStat(max, min, average, change)
    }
}