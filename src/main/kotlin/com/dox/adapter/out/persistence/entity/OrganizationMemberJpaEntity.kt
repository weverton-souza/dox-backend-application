package com.dox.adapter.out.persistence.entity

import com.dox.domain.enum.MemberRole
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "organization_members", schema = "public")
@EntityListeners(AuditingEntityListener::class)
class OrganizationMemberJpaEntity(
    @Id
    @Column(name = "id", updatable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "organization_id", nullable = false)
    var organizationId: UUID = UUID.randomUUID(),
    @Column(name = "user_id", nullable = false)
    var userId: UUID = UUID.randomUUID(),
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    var role: MemberRole = MemberRole.MEMBER,
    @CreatedDate
    @Column(name = "joined_at", updatable = false)
    var joinedAt: LocalDateTime? = null,
)
