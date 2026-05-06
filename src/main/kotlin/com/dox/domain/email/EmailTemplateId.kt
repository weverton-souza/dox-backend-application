package com.dox.domain.email

enum class EmailTemplateId(
    val templateName: String,
    val category: EmailCategory,
    val followupLevel: FollowupLevel? = null,
) {
    WELCOME("welcome", EmailCategory.DEFAULT),
    FORM_INVITE("form-invite", EmailCategory.DEFAULT),
    WORKSPACE_INVITE("workspace-invite", EmailCategory.DEFAULT),
    BILLING_INVOICE("billing-invoice", EmailCategory.DEFAULT),
    PASSWORD_RESET("password-reset", EmailCategory.SECURITY),
    REPORT_FINALIZED("report-finalized", EmailCategory.SUCCESS),
    FORM_FOLLOWUP_SOFT("form-followup-soft", EmailCategory.FOLLOWUP, FollowupLevel.SOFT),
    FORM_FOLLOWUP_MEDIUM("form-followup-medium", EmailCategory.FOLLOWUP, FollowupLevel.MEDIUM),
    FORM_FOLLOWUP_URGENT("form-followup-urgent", EmailCategory.FOLLOWUP, FollowupLevel.URGENT),
    ;

    companion object {
        fun fromTemplateName(name: String): EmailTemplateId? = entries.firstOrNull { it.templateName == name }
    }
}
