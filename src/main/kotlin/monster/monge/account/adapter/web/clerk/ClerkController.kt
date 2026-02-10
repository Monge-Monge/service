package monster.monge.account.adapter.web.clerk

import monster.monge.account.application.provided.AccountRegister
import monster.monge.account.domain.AccountRegisterRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/webhooks/clerk")
class ClerkController(
    private val accountRegister: AccountRegister,
) {
    @PostMapping
    fun handleWebhook(
        @VerifiedClerkPayload request: AccountRegisterRequest
    ): ResponseEntity<Unit> {
        accountRegister.register(request)
        return ResponseEntity.ok().build()
    }
}