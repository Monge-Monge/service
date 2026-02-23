package monster.monge.post.application.required

import monster.monge.post.domain.Post
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long> {
    fun findAllByAccountIdInOrderByCreatedAtDesc(accountIds: List<Long>, pageable: Pageable): List<Post>
}
