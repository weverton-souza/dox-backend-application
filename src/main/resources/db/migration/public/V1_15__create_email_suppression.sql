CREATE TABLE public.email_suppression (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(320) NOT NULL,
    reason VARCHAR(20) NOT NULL CHECK (reason IN ('HARD_BOUNCE', 'COMPLAINT', 'MANUAL', 'INVALID')),
    notes TEXT NULL,
    suppressed_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_email_suppression_email ON public.email_suppression(LOWER(email));
