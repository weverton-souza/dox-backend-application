package com.dox.adapter.`in`.rest.impl

import com.dox.adapter.`in`.rest.dto.customer.CustomerCalendarEventResponse
import com.dox.adapter.`in`.rest.resource.CustomerCalendarEventResource
import com.dox.application.port.input.CustomerUseCase
import com.dox.config.security.RequiresModule
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime

@RestController
@RequiresModule("customers")
class CustomerCalendarEventResourceImpl(
    private val customerUseCase: CustomerUseCase,
) : CustomerCalendarEventResource {
    override fun findByDateRange(
        from: OffsetDateTime,
        to: OffsetDateTime,
    ): ResponseEntity<List<CustomerCalendarEventResponse>> {
        val fromDate = from.toLocalDateTime()
        val toDate = to.toLocalDateTime()

        val eventsWithNames = customerUseCase.findAllEventsByDateRange(fromDate, toDate)

        val response =
            eventsWithNames.map { (event, customerName) ->
                CustomerCalendarEventResponse(
                    id = event.id,
                    customerId = event.customerId,
                    customerName = customerName,
                    type = event.type,
                    title = event.title,
                    description = event.description,
                    date = event.date,
                    createdAt = event.createdAt,
                )
            }

        return responseEntity(response)
    }
}
