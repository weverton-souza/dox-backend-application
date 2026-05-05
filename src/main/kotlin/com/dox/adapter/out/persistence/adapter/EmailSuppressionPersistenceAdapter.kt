package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.EmailSuppressionJpaEntity
import com.dox.adapter.out.persistence.repository.EmailSuppressionJpaRepository
import com.dox.application.port.output.EmailSuppressionPersistencePort
import com.dox.domain.email.EmailSuppression
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class EmailSuppressionPersistenceAdapter(
    private val repository: EmailSuppressionJpaRepository,
) : EmailSuppressionPersistencePort {
    @Transactional
    override fun save(suppression: EmailSuppression): EmailSuppression {
        val existing = repository.findByEmailIgnoreCase(suppression.email)
        val entity =
            (existing ?: EmailSuppressionJpaEntity(id = suppression.id)).apply {
                email = suppression.email
                reason = suppression.reason
                notes = suppression.notes
            }
        return repository.save(entity).toDomain()
    }

    override fun isSuppressed(email: String): Boolean = repository.existsByEmailIgnoreCase(email)

    override fun findByEmail(email: String): EmailSuppression? = repository.findByEmailIgnoreCase(email)?.toDomain()

    @Transactional
    override fun delete(email: String): Boolean = repository.deleteByEmailIgnoreCase(email) > 0

    private fun EmailSuppressionJpaEntity.toDomain() =
        EmailSuppression(
            id = id,
            email = email,
            reason = reason,
            notes = notes,
            suppressedAt = suppressedAt ?: LocalDateTime.now(),
        )
}
