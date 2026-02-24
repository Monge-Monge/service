package monster.monge.profile.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "profiles")
class Profile(
    val accountId: Long,
    var nickname: String,
    var profileImageUrl: String? = null,
    var goalWeight: BigDecimal? = null,
    var height: BigDecimal? = null,
    var bio: String? = null,
    var isPublic: Boolean = false,
    @Id @GeneratedValue
    val id: Long? = null,
)
