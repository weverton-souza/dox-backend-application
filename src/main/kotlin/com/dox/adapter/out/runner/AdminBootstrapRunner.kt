package com.dox.adapter.out.runner

import com.dox.application.port.output.AdminUserPersistencePort
import com.dox.application.port.output.PasswordEncoderPort
import com.dox.domain.enum.AdminRole
import com.dox.domain.model.AdminUser
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["dox.admin.bootstrap.enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
class AdminBootstrapRunner(
    private val adminUserPersistencePort: AdminUserPersistencePort,
    private val passwordEncoderPort: PasswordEncoderPort,
    @param:Value("\${dox.admin.bootstrap.email:wever@doxbr.com}")
    private val email: String,
    @param:Value("\${dox.admin.bootstrap.password:admin1234}")
    private val password: String,
    @param:Value("\${dox.admin.bootstrap.name:Wever}")
    private val name: String,
) : ApplicationRunner {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(args: ApplicationArguments) {
        if (adminUserPersistencePort.findByEmail(email) != null) return

        adminUserPersistencePort.save(
            AdminUser(
                email = email,
                name = name,
                passwordHash = passwordEncoderPort.encode(password),
                role = AdminRole.SUPER_ADMIN,
            ),
        )
        log.info("Admin bootstrap criado: {}", email)
    }
}
