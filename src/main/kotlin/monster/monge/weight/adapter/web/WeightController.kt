package monster.monge.weight.adapter.web

import monster.monge.global.extension.accountId
import monster.monge.weight.application.provided.WeightFinder
import monster.monge.weight.application.provided.WeightRecorder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/weights")
class WeightController(
    private val weightRecorder: WeightRecorder,
    private val weightFinder: WeightFinder,
) {

    @PostMapping
    fun create(
        auth: JwtAuthenticationToken,
        @RequestBody request: WeightCreateRequest,
    ): ResponseEntity<WeightResponse> {
        val accountId = auth.accountId()
        val weight = weightRecorder.record(accountId, request.value, request.recordedAt, request.memo)
        return ResponseEntity.status(HttpStatus.CREATED).body(WeightResponse.from(weight))
    }

    @GetMapping
    fun findAll(auth: JwtAuthenticationToken): ResponseEntity<List<WeightResponse>> {
        val accountId = auth.accountId()
        val weights = weightFinder.findAll(accountId)
        return ResponseEntity.ok(weights.map { WeightResponse.from(it) })
    }

    @GetMapping("/{id}")
    fun findById(
        auth: JwtAuthenticationToken,
        @PathVariable id: Long,
    ): ResponseEntity<WeightResponse> {
        val accountId = auth.accountId()
        val weight = weightFinder.findById(accountId, id)
        return ResponseEntity.ok(WeightResponse.from(weight))
    }

    @PutMapping("/{id}")
    fun update(
        auth: JwtAuthenticationToken,
        @PathVariable id: Long,
        @RequestBody request: WeightUpdateRequest,
    ): ResponseEntity<WeightResponse> {
        val accountId = auth.accountId()
        val weight = weightRecorder.update(accountId, id, request.value, request.recordedAt, request.memo)
        return ResponseEntity.ok(WeightResponse.from(weight))
    }

    @DeleteMapping("/{id}")
    fun delete(
        auth: JwtAuthenticationToken,
        @PathVariable id: Long,
    ): ResponseEntity<Unit> {
        val accountId = auth.accountId()
        weightRecorder.delete(accountId, id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/graph")
    fun graph(
        auth: JwtAuthenticationToken,
        @RequestParam period: String,
    ): ResponseEntity<List<WeightResponse>> {
        val accountId = auth.accountId()
        val weights = weightFinder.graph(accountId, period)
        return ResponseEntity.ok(weights.map { WeightResponse.from(it) })
    }

    @GetMapping("/stats")
    fun stats(auth: JwtAuthenticationToken): ResponseEntity<WeightStatResponse> {
        val accountId = auth.accountId()
        val stat = weightFinder.stats(accountId)
        return ResponseEntity.ok(WeightStatResponse.from(stat))
    }
}
