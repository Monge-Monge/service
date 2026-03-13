package monster.monge.global.extension

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

fun JwtAuthenticationToken.accountId(): Long {
    return accountIdOrNull() ?: throw BadCredentialsException("sub_id claim not found")
}

fun JwtAuthenticationToken.accountIdOrNull(): Long? {
    return this.token.claims["sub_id"]?.toString()?.toLongOrNull()
}