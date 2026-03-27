package com.dox.application.port.output

import com.dox.application.port.input.PreviousSectionContext
import com.dox.application.port.input.QuantitativeDataPayload
import com.dox.domain.enum.Vertical
import com.dox.domain.model.Customer
import com.dox.domain.model.FormResponse
import com.dox.domain.model.ProfessionalSettings
import com.dox.domain.model.ReportTemplate

interface AiSectionPromptPort {

    fun buildContext(
        customer: Customer?,
        formResponses: List<FormResponse>?,
        template: ReportTemplate?,
        professional: ProfessionalSettings?,
        quantitativeData: QuantitativeDataPayload? = null
    ): String

    fun buildUserPrompt(sectionType: String, vertical: Vertical? = null): String

    fun buildUserPromptWithContext(
        sectionType: String,
        previousSections: List<PreviousSectionContext>,
        vertical: Vertical? = null
    ): String
}
