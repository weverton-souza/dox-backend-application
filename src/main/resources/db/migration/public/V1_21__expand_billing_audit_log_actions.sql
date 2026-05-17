ALTER TABLE billing_audit_log DROP CONSTRAINT billing_audit_log_action_check;

ALTER TABLE billing_audit_log ADD CONSTRAINT billing_audit_log_action_check
    CHECK (action IN (
        'GRANT_MODULE',
        'EXTEND_TRIAL',
        'LOCK_PRICE',
        'UNLOCK_PRICE',
        'EDIT_MODULE_PRICE',
        'CREATE_BUNDLE',
        'EDIT_BUNDLE',
        'ARCHIVE_BUNDLE',
        'CREATE_ADDON',
        'EDIT_ADDON',
        'ARCHIVE_ADDON',
        'CREATE_PROMOTION',
        'UPDATE_PROMOTION',
        'ARCHIVE_PROMOTION'
    ));
