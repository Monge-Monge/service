package monster.monge.account.infrastructure.persistence

import monster.monge.account.domain.Account
import org.springframework.data.jpa.repository.JpaRepository

interface JpaAccountRepository : JpaRepository<Account, Long> {
}
