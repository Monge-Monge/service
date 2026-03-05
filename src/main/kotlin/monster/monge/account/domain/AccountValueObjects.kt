package monster.monge.account.domain

@JvmInline
value class Email(val value: String) {
    init {
        require(value.matches(Regex("""^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$"""))) {
            "Invalid email format: $value"
        }
    }
}

@JvmInline
value class ProviderId(val value: String) {
    init {
        require(value.isNotBlank()) {
            "ProviderId must not be blank"
        }
    }
}
