package monster.monge.account.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.domain.AbstractAggregateRoot

@Entity
@Table(name = "accounts")
class Account(
    val email: String,
    val providerId: String,
    @Id
    @GeneratedValue
    val id: Long? = null,
): AbstractAggregateRoot<Account>() {

    init {
        Email(email)
        ProviderId(providerId)
        registerEvent(AccountRegistered(this))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Account) return false
        return email == other.email && providerId == other.providerId
    }

    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + providerId.hashCode()
        return result
    }

    companion object {
        fun from(request: AccountRegisterRequest): Account {
            Email(request.email)
            ProviderId(request.providerId)
            return Account(request.email, request.providerId)
        }
    }
}
