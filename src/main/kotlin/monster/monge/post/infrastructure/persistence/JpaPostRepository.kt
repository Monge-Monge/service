package monster.monge.post.infrastructure.persistence

import monster.monge.post.domain.Post
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface JpaPostRepository : JpaRepository<Post, Long> {
    fun findAllByAccountIdInOrderByCreatedAtDesc(accountIds: List<Long>, pageable: Pageable): List<Post>
}
