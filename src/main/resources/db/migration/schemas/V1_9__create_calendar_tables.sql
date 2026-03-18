CREATE TABLE event_tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    color VARCHAR(7) NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE calendar_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    summary VARCHAR(500) NOT NULL,
    description TEXT,
    location VARCHAR(500),
    start_date DATE,
    start_date_time TIMESTAMPTZ,
    start_time_zone VARCHAR(100),
    end_date DATE,
    end_date_time TIMESTAMPTZ,
    end_time_zone VARCHAR(100),
    all_day BOOLEAN DEFAULT false,
    tag_id UUID REFERENCES event_tags(id),
    customer_id UUID REFERENCES customers(id),
    status VARCHAR(20) DEFAULT 'confirmed',
    recurrence JSONB,
    reminders JSONB,
    google_event_id VARCHAR(1024),
    ical_uid VARCHAR(1024),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_calendar_events_start ON calendar_events(start_date_time);
CREATE INDEX idx_calendar_events_tag ON calendar_events(tag_id);
CREATE INDEX idx_calendar_events_customer ON calendar_events(customer_id);
