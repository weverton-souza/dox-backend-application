package com.dox.domain.email

enum class EmailTemplateId(val templateName: String) {
    WELCOME("welcome"),
    PASSWORD_RESET("password-reset"),
    WORKSPACE_INVITE("workspace-invite"),
    FORM_REMINDER_D1("form-reminder-d1"),
    FORM_REMINDER_D3("form-reminder-d3"),
    FORM_REMINDER_D7("form-reminder-d7"),
    ;

    companion object {
        fun fromTemplateName(name: String): EmailTemplateId? = entries.firstOrNull { it.templateName == name }
    }
}
