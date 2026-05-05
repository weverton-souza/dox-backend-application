ALTER TABLE public.users
    ADD COLUMN email_verified_at TIMESTAMP NULL,
    ADD COLUMN email_verification_token VARCHAR(255) NULL,
    ADD COLUMN email_verification_token_expires_at TIMESTAMP NULL;

UPDATE public.users
SET email_verified_at = COALESCE(created_at, NOW())
WHERE email_verified_at IS NULL;

CREATE UNIQUE INDEX idx_users_email_verification_token
    ON public.users(email_verification_token)
    WHERE email_verification_token IS NOT NULL;
