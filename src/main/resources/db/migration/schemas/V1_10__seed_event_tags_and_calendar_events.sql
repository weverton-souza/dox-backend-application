INSERT INTO event_tags (id, name, color) VALUES
('e1a00001-0000-4000-8000-000000000001', 'Consulta', '#007AFF'),
('e1a00002-0000-4000-8000-000000000002', 'Retorno', '#34C759'),
('e1a00003-0000-4000-8000-000000000003', 'Avaliação', '#AF52DE'),
('e1a00004-0000-4000-8000-000000000004', 'Supervisão', '#FF9500'),
('e1a00005-0000-4000-8000-000000000005', 'Devolutiva', '#FF2D55'),
('e1a00006-0000-4000-8000-000000000006', 'Atendimento', '#5856D6'),
('e1a00007-0000-4000-8000-000000000007', 'Observação', '#8E8E93'),
('e1a00008-0000-4000-8000-000000000008', 'Relatório Técnico', '#FF6B35'),
('e1a00009-0000-4000-8000-000000000009', 'Laudo', '#30B0C7'),
('e1a0000a-0000-4000-8000-00000000000a', 'Reunião', '#FFD60A'),
('e1a0000b-0000-4000-8000-00000000000b', 'Encaminhamento', '#AC8E68');

INSERT INTO calendar_events (id, summary, tag_id, customer_id, start_date_time, start_time_zone, end_date_time, end_time_zone, all_day, status) VALUES
('c1e00001-0000-4000-8000-000000000001', 'Sessão com Maria', 'e1a00001-0000-4000-8000-000000000001', 'c0000001-0000-0000-0000-000000000001',
  (date_trunc('month', now()) + interval '2 days' + time '09:00')::timestamptz, 'America/Sao_Paulo',
  (date_trunc('month', now()) + interval '2 days' + time '10:00')::timestamptz, 'America/Sao_Paulo', false, 'confirmed'),

('c1e00002-0000-4000-8000-000000000002', 'Retorno Maria', 'e1a00002-0000-4000-8000-000000000002', 'c0000001-0000-0000-0000-000000000001',
  (date_trunc('month', now()) + interval '9 days' + time '14:00')::timestamptz, 'America/Sao_Paulo',
  (date_trunc('month', now()) + interval '9 days' + time '15:00')::timestamptz, 'America/Sao_Paulo', false, 'confirmed'),

('c1e00003-0000-4000-8000-000000000003', 'Avaliação psicométrica', 'e1a00003-0000-4000-8000-000000000003', 'c0000001-0000-0000-0000-000000000001',
  (date_trunc('month', now()) + interval '15 days' + time '10:00')::timestamptz, 'America/Sao_Paulo',
  (date_trunc('month', now()) + interval '15 days' + time '11:30')::timestamptz, 'America/Sao_Paulo', false, 'confirmed'),

('c1e00004-0000-4000-8000-000000000004', 'Supervisão clínica', 'e1a00004-0000-4000-8000-000000000004', NULL,
  (date_trunc('month', now()) + interval '12 days' + time '16:00')::timestamptz, 'America/Sao_Paulo',
  (date_trunc('month', now()) + interval '12 days' + time '17:30')::timestamptz, 'America/Sao_Paulo', false, 'confirmed'),

('c1e00005-0000-4000-8000-000000000005', 'Devolutiva Maria', 'e1a00005-0000-4000-8000-000000000005', 'c0000001-0000-0000-0000-000000000001',
  (date_trunc('month', now()) + interval '22 days' + time '09:00')::timestamptz, 'America/Sao_Paulo',
  (date_trunc('month', now()) + interval '22 days' + time '10:00')::timestamptz, 'America/Sao_Paulo', false, 'confirmed'),

('c1e00006-0000-4000-8000-000000000006', 'Sessão com Maria', 'e1a00001-0000-4000-8000-000000000001', 'c0000001-0000-0000-0000-000000000001',
  (date_trunc('month', now()) + interval '16 days' + time '09:00')::timestamptz, 'America/Sao_Paulo',
  (date_trunc('month', now()) + interval '16 days' + time '10:00')::timestamptz, 'America/Sao_Paulo', false, 'confirmed'),

('c1e00007-0000-4000-8000-000000000007', 'Reunião de equipe', 'e1a00004-0000-4000-8000-000000000004', NULL,
  (date_trunc('month', now()) + interval '25 days' + time '14:00')::timestamptz, 'America/Sao_Paulo',
  (date_trunc('month', now()) + interval '25 days' + time '15:30')::timestamptz, 'America/Sao_Paulo', false, 'confirmed');
