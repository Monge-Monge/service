package monster.monge.account.adapter.web.clerk

import monster.monge.account.domain.AccountRegistered
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ClerkAccountRegisteredHandler(
    private val clerkClient: ClerkClient
) {

    @EventListener
    fun handle(event: AccountRegistered) {
        clerkClient.mergeAndUpdate(event.providerId, mapOf("sub_id" to event.userId))
    }
}