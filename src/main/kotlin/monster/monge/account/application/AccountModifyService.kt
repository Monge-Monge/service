package monster.monge.account.application

import monster.monge.account.application.provided.AccountRegister
import monster.monge.account.application.required.AccountRepository
import monster.monge.account.domain.Account
import monster.monge.account.domain.AccountRegisterRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AccountModifyService(
    private val accountRepository: AccountRepository,
) : AccountRegister {
    override fun register(request: AccountRegisterRequest): Account {
        val account = Account.from(request)
        return accountRepository.save(account)
    }
}