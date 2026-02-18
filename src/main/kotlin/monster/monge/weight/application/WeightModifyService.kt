package monster.monge.weight.application

import monster.monge.weight.application.provided.WeightFinder
import monster.monge.weight.application.provided.WeightRecorder
import monster.monge.weight.application.required.WeightRepository
import monster.monge.weight.domain.Weight
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
@Transactional
class WeightModifyService(
    private val weightRepository: WeightRepository,
    private val weightFinder: WeightFinder,
) : WeightRecorder {

    override fun record(accountId: Long, value: BigDecimal, recordedAt: LocalDate, memo: String?): Weight {
        val weight = Weight(accountId, value, recordedAt, memo)
        return weightRepository.save(weight)
    }

    override fun update(accountId: Long, id: Long, value: BigDecimal, recordedAt: LocalDate, memo: String?): Weight {
        val weight = weightFinder.findById(accountId, id)
        val updated = weight.update(value, recordedAt, memo)
        return weightRepository.save(updated)
    }

    override fun delete(accountId: Long, id: Long) {
        val weight = weightFinder.findById(accountId, id)
        weightRepository.delete(weight)
    }
}
