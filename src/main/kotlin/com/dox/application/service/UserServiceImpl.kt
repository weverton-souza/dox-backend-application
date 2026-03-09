package com.dox.application.service

import com.dox.application.port.input.UpdateUserCommand
import com.dox.application.port.input.UserInfo
import com.dox.application.port.input.UserUseCase
import com.dox.application.port.output.UserPersistencePort
import com.dox.domain.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserServiceImpl(
    private val userPersistencePort: UserPersistencePort
) : UserUseCase {

    override fun getMe(userId: UUID): UserInfo {
        val user = userPersistencePort.findById(userId)
            ?: throw ResourceNotFoundException("Usuário não encontrado")
        return UserInfo(
            id = user.id,
            email = user.email,
            name = user.name,
            personalTenantId = user.personalTenantId
        )
    }

    @Transactional
    override fun updateMe(command: UpdateUserCommand): UserInfo {
        val user = userPersistencePort.findById(command.userId)
            ?: throw ResourceNotFoundException("Usuário não encontrado")

        val updated = userPersistencePort.save(user.copy(name = command.name))
        return UserInfo(
            id = updated.id,
            email = updated.email,
            name = updated.name,
            personalTenantId = updated.personalTenantId
        )
    }
}
