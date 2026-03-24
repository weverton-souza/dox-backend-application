package com.dox.application.port.output

import com.dox.domain.model.CustomerFile
import java.util.UUID

interface CustomerFilePersistencePort {
    fun save(customerFile: CustomerFile): CustomerFile
    fun findByCustomerId(customerId: UUID): List<CustomerFile>
    fun findById(id: UUID): CustomerFile?
}
