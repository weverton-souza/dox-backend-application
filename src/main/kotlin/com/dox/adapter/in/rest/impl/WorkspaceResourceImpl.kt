package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.workspace.CreateOrganizationRequest
import com.dox.adapter.`in`.rest.dto.workspace.InviteMemberRequest
import com.dox.adapter.`in`.rest.dto.workspace.WorkspaceResponse
import com.dox.adapter.`in`.rest.resource.WorkspaceResource
import com.dox.application.port.input.CreateOrganizationCommand
import com.dox.application.port.input.InviteMemberCommand
import com.dox.application.port.input.WorkspaceUseCase
import com.dox.shared.ContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class WorkspaceResourceImpl(
    private val workspaceUseCase: WorkspaceUseCase
) : WorkspaceResource {
    override fun listWorkspaces(): ResponseEntity<List<WorkspaceResponse>> {
        val userId = ContextHolder.getUserIdOrThrow()
        val workspaces = workspaceUseCase.listWorkspaces(userId).map {
            WorkspaceResponse(
                tenantId = it.tenantId,
                name = it.name,
                type = it.type,
                vertical = it.vertical,
                role = it.role
            )
        }
        return responseEntity(workspaces)
    }

    override fun createOrganization(request: CreateOrganizationRequest): ResponseEntity<WorkspaceResponse> {
        val userId = ContextHolder.getUserIdOrThrow()
        val result = workspaceUseCase.createOrganization(
            CreateOrganizationCommand(
                userId = userId,
                name = request.name,
                description = request.description,
                vertical = request.vertical
            )
        )
        return responseEntity(
            WorkspaceResponse(
                tenantId = result.tenantId,
                name = result.name,
                type = result.type,
                vertical = result.vertical,
                role = result.role
            ),
            HttpStatus.CREATED
        )
    }

    override fun inviteMember(organizationId: UUID, request: InviteMemberRequest): ResponseEntity<Void> {
        workspaceUseCase.inviteMember(
            InviteMemberCommand(
                organizationId = organizationId,
                email = request.email,
                role = request.role
            )
        )
        return noContent()
    }
}
