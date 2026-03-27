package com.dox.application.service

import com.dox.application.port.input.UpdateUserCommand
import com.dox.application.port.input.UserInfo
import com.dox.application.port.input.UserUseCase
import com.dox.application.port.output.UserPersistencePort
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserServiceImpl(
    private val userPersistencePort: UserPersistencePort
) : UserUseCase {

    override fun getMe(userId: UUID): UserInfo {
        val user = userPersistencePort.findById(userId)
            ?: throw ResourceNotFoundException("Usuário", userId.toString())
        return user.toUserInfo()
    }

    @Transactional
    override fun updateMe(command: UpdateUserCommand): UserInfo {
        val user = userPersistencePort.findById(command.userId)
            ?: throw ResourceNotFoundException("Usuário", command.userId.toString())

        val updated = userPersistencePort.save(user.copy(name = command.name))
        return updated.toUserInfo()
    }

    private fun User.toUserInfo() = UserInfo(
        id = id,
        email = email,
        name = name,
        personalTenantId = personalTenantId
    )
}
