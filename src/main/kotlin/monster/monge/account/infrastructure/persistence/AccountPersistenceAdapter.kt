package monster.monge.account.infrastructure.persistence

import monster.monge.account.domain.Account
import monster.monge.account.domain.AccountRepository
import org.springframework.stereotype.Repository

@Repository
class AccountPersistenceAdapter(
    private val jpaAccountRepository: JpaAccountRepository
) : AccountRepository {
    override fun save(account: Account): Account = jpaAccountRepository.save(account)
    override fun findById(id: Long): Account? = jpaAccountRepository.findById(id).orElse(null)
}
