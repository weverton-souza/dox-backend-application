package com.dox.application.service

import com.dox.application.port.input.AdminStudentVerificationUseCase
import com.dox.application.port.input.CreateStudentVerificationCommand
import com.dox.application.port.input.PromotionUseCase
import com.dox.application.port.output.StudentVerificationPersistencePort
import com.dox.application.port.output.TenantPersistencePort
import com.dox.application.port.output.UserPersistencePort
import com.dox.domain.billing.StudentVerification
import com.dox.domain.billing.StudentVerificationStatus
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class AdminStudentVerificationServiceImpl(
    private val studentVerificationPersistencePort: StudentVerificationPersistencePort,
    private val tenantPersistencePort: TenantPersistencePort,
    private val userPersistencePort: UserPersistencePort,
    private val promotionUseCase: PromotionUseCase,
) : AdminStudentVerificationUseCase {
    companion object {
        private const val STUDENT_PROMOTION_CODE = "STUDENT"
    }

    @Transactional(readOnly = true)
    override fun list(
        status: StudentVerificationStatus?,
        pageable: Pageable,
    ): Page<StudentVerification> = studentVerificationPersistencePort.findPaginated(status, pageable)

    @Transactional(readOnly = true)
    override fun getById(id: UUID): StudentVerification =
        studentVerificationPersistencePort.findById(id)
            ?: throw ResourceNotFoundException("StudentVerification", id.toString())

    @Transactional
    override fun createManual(
        command: CreateStudentVerificationCommand,
        actorAdminId: UUID,
    ): StudentVerification {
        if (command.documentUrl.isBlank()) {
            throw BusinessException("URL do documento é obrigatória")
        }

        tenantPersistencePort.findById(command.tenantId)
            ?: throw ResourceNotFoundException("Tenant", command.tenantId.toString())
        userPersistencePort.findById(command.userId)
            ?: throw ResourceNotFoundException("User", command.userId.toString())

        return studentVerificationPersistencePort.save(
            StudentVerification(
                tenantId = command.tenantId,
                userId = command.userId,
                documentUrl = command.documentUrl,
                institution = command.institution,
                course = command.course,
                expectedGraduation = command.expectedGraduation,
                status = StudentVerificationStatus.PENDING,
                notes = command.notes,
            ),
        )
    }

    @Transactional
    override fun approve(
        id: UUID,
        notes: String?,
        actorAdminId: UUID,
    ): StudentVerification {
        val existing = getById(id)
        if (existing.status != StudentVerificationStatus.PENDING) {
            throw BusinessException("Verificação já foi processada")
        }

        val alreadyApplied =
            promotionUseCase.listActive(existing.tenantId)
                .any { it.promotion.code == STUDENT_PROMOTION_CODE }

        if (!alreadyApplied) {
            try {
                promotionUseCase.applyCoupon(existing.tenantId, STUDENT_PROMOTION_CODE, actorAdminId)
            } catch (e: BusinessException) {
                if (e.message?.contains("inválido") == true) {
                    throw BusinessException(
                        "Promoção '$STUDENT_PROMOTION_CODE' não cadastrada — crie primeiro em /admin/promotions",
                    )
                }
                throw e
            }
        }

        return studentVerificationPersistencePort.save(
            existing.copy(
                status = StudentVerificationStatus.APPROVED,
                reviewedByAdminId = actorAdminId,
                reviewedAt = LocalDateTime.now(),
                notes = notes ?: existing.notes,
                rejectionReason = null,
            ),
        )
    }

    @Transactional
    override fun reject(
        id: UUID,
        rejectionReason: String,
        actorAdminId: UUID,
    ): StudentVerification {
        if (rejectionReason.isBlank()) {
            throw BusinessException("Motivo da rejeição é obrigatório")
        }
        val existing = getById(id)
        if (existing.status != StudentVerificationStatus.PENDING) {
            throw BusinessException("Verificação já foi processada")
        }

        return studentVerificationPersistencePort.save(
            existing.copy(
                status = StudentVerificationStatus.REJECTED,
                reviewedByAdminId = actorAdminId,
                reviewedAt = LocalDateTime.now(),
                rejectionReason = rejectionReason,
            ),
        )
    }
}
