package monster.monge.account.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "accounts")
class Account(
    val email: String,
    val providerId: String,
    @Id
    @GeneratedValue
    val id: Long? = null,
) {
    companion object {
        fun register(request: AccountRegisterRequest) =
            Account(request.email, request.providerId)
    }
}
