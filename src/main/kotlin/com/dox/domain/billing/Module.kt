package com.dox.domain.billing

enum class Module(
    val id: String,
    val displayName: String,
    val basePriceMonthlyCents: Int,
    val dependencies: Set<String> = emptySet(),
    val gracePeriodDays: Int = 30,
    val gracefulDegradation: DegradationMode = DegradationMode.BLOCKED,
) {
    REPORTS("reports", "Relatórios", 9900, gracefulDegradation = DegradationMode.READ_ONLY),
    CUSTOMERS("customers", "Pacientes", 5900, gracefulDegradation = DegradationMode.READ_ONLY),
    FORMS("forms", "Formulários", 5900, gracefulDegradation = DegradationMode.READ_ONLY),
    CALENDAR("calendar", "Calendário", 3900, gracefulDegradation = DegradationMode.READ_ONLY),
    AI_LIGHT("ai_light", "DOX IA", 9900, dependencies = setOf("reports"), gracePeriodDays = 7),
    AI_PRO("ai_pro", "DOX IA Pro", 19900, dependencies = setOf("reports"), gracePeriodDays = 7),
    PAYMENTS("payments", "DOX Pagamentos", 0, dependencies = setOf("customers"), gracePeriodDays = 14),
    FINANCIAL("financial", "DOX Financeiro", 2900, dependencies = setOf("payments")),
    FILES_OCR("files_ocr", "Arquivos+OCR", 2900, dependencies = setOf("customers"), gracePeriodDays = 14),
    ;

    companion object {
        fun fromId(id: String): Module? = entries.firstOrNull { it.id == id }
    }
}
