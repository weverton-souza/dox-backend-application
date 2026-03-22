package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.workspace.CreateOrganizationRequest
import com.dox.adapter.`in`.rest.dto.workspace.InviteMemberRequest
import com.dox.adapter.`in`.rest.dto.workspace.WorkspaceResponse
import jakarta.validation.Valid
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Tag(name = "Workspaces", description = "Gerenciamento de workspaces e organizações")
@RequestMapping("/workspaces")
interface WorkspaceResource : BaseResource {

    @Operation(summary = "Listar workspaces do usuário")
    @GetMapping
    fun listWorkspaces(): ResponseEntity<List<WorkspaceResponse>>

    @Operation(summary = "Criar nova organização")
    @PostMapping("/organizations")
    fun createOrganization(@Valid @RequestBody request: CreateOrganizationRequest): ResponseEntity<WorkspaceResponse>

    @Operation(summary = "Convidar membro para organização")
    @PostMapping("/organizations/{organizationId}/members")
    fun inviteMember(
        @PathVariable organizationId: UUID,
        @Valid @RequestBody request: InviteMemberRequest
    ): ResponseEntity<Void>
}
