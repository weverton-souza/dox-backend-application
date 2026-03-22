package com.dox.adapter.`in`.rest.dto.workspace

import com.dox.domain.enum.MemberRole
import com.dox.domain.enum.TenantType
import com.dox.domain.enum.Vertical
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class WorkspaceResponse(
    val tenantId: UUID,
    val name: String,
    val type: TenantType,
    val vertical: Vertical,
    val role: MemberRole?
)

data class CreateOrganizationRequest(
    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    val name: String,

    @field:Size(max = 2000, message = "Descrição deve ter no máximo 2000 caracteres")
    val description: String? = null,

    val vertical: Vertical = Vertical.GENERAL
)

data class InviteMemberRequest(
    @field:NotBlank(message = "Email é obrigatório")
    @field:Email(message = "Email inválido")
    val email: String,

    val role: MemberRole = MemberRole.MEMBER
)
