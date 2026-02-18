package monster.monge.global.extension

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

fun JwtAuthenticationToken.accountId(): Long {
    return  accountIdOrNull() ?: throw IllegalStateException("accountId claim not found")
}

fun JwtAuthenticationToken.accountIdOrNull(): Long? {
    return this.token.claims["accountId"] as Long?
}