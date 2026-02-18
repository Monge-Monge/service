package monster.monge.account.adapter.web.clerk

import monster.monge.account.application.provided.AccountRegister
import monster.monge.account.domain.AccountRegisterRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tools.jackson.databind.ObjectMapper

@RestController
@RequestMapping("/webhooks/clerk")
class ClerkController(
    private val accountRegister: AccountRegister,
    private val svixSignatureValidator: SvixSignatureValidator,
    private val objectMapper: ObjectMapper
) {
    @PostMapping
    fun handleWebhook(
        @RequestHeader("svix-id") svixId: String,
        @RequestHeader("svix-signature") svixSignature: String,
        @RequestHeader("svix-timestamp") svixTimestamp: String,
        @RequestBody payload: String
    ): ResponseEntity<Unit> {

        svixSignatureValidator.verify(payload, svixId, svixSignature, svixTimestamp)

        val event = objectMapper.readValue(payload, ClerkWebhookEvent::class.java)

        if (event.type != "user.created") {
            val userData = event.data
            val accountRegisterRequest = AccountRegisterRequest(
                userData.emailAddresses.first().emailAddress,
                userData.id)
            accountRegister.register(accountRegisterRequest)
        }

        return ResponseEntity.ok().build()
    }
}