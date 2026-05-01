package com.dox.application.port.output

import com.dox.domain.billing.StudentVerification
import com.dox.domain.billing.StudentVerificationStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface StudentVerificationPersistencePort {
    fun findById(id: UUID): StudentVerification?

    fun save(verification: StudentVerification): StudentVerification

    fun findPaginated(
        status: StudentVerificationStatus?,
        pageable: Pageable,
    ): Page<StudentVerification>
}
