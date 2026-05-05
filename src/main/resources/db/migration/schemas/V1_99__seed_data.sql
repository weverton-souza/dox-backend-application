-- Professional Settings
INSERT INTO professional_settings (name, crp, specialization, phone, email, contact_items)
SELECT 'Dra. Ana Silva', '06/12345', 'Neuropsicologia', '(11) 98765-4321', 'ana.silva@clinica.com.br',
       '[{"type":"instagram","value":"@dra.anasilva"},{"type":"website","value":"www.clinicaanasilva.com.br"}]'::JSONB
WHERE NOT EXISTS (SELECT 1 FROM professional_settings);

-- Customers (3)
INSERT INTO customers (id, data, deleted) VALUES
('b5e59243-c260-4ff4-891d-cdb97520d29d',
 '{"name":"Maria Aparecida Santos","cpf":"983.535.674-23","birthDate":"1985-03-15","age":"40 anos","education":"Ensino Superior Completo","profession":"Professora","motherName":"Helena Maria dos Santos","fatherName":"Roberto Carlos Santos","phone":"(11) 99876-5432","email":"maria.santos@email.com","addressStreet":"Rua das Flores, 100","addressCity":"São Paulo","addressState":"SP","addressZipCode":"01234-567","chiefComplaint":"Ansiedade generalizada com episódios de insônia há 8 meses","diagnosis":"Transtorno de Ansiedade Generalizada (F41.1)","medications":"Escitalopram 10mg","referralDoctor":"Dr. Paulo Henrique Mendes - CRM/SP 123456","guardianName":"","guardianRelationship":""}'::JSONB,
 false),

('46e24bd1-7cdd-4b84-b976-daaa11c1281b',
 '{"name":"José Carlos Oliveira","cpf":"093.646.184-59","birthDate":"1978-07-22","age":"47 anos","education":"Ensino Superior Completo","profession":"Engenheiro Civil","motherName":"Conceição de Oliveira","fatherName":"Antônio Carlos Oliveira","phone":"(11) 98234-5678","email":"jose.oliveira@email.com","addressStreet":"Av. Paulista, 1500","addressCity":"São Paulo","addressState":"SP","addressZipCode":"01310-100","chiefComplaint":"Dificuldade de concentração e lapsos de memória","diagnosis":"","medications":"","referralDoctor":"Dra. Carla Ferreira - CRM/SP 654321"}'::JSONB,
 false),

('9b687cc1-2aaa-423a-afbc-d2ac6ad244b1',
 '{"name":"Fernanda Rodrigues Lima","cpf":"584.506.073-70","birthDate":"1992-11-08","age":"33 anos","education":"Pós-Graduação","profession":"Psicóloga","motherName":"Sandra Rodrigues","fatherName":"Carlos Lima","phone":"(21) 99123-4567","email":"fernanda.lima@email.com","addressStreet":"Rua Copacabana, 45","addressCity":"Rio de Janeiro","addressState":"RJ","addressZipCode":"22020-001","chiefComplaint":"Episódios de tristeza profunda e falta de motivação","diagnosis":"","medications":"Sertralina 50mg","referralDoctor":"Dr. Marcos Almeida - CRM/RJ 987654"}'::JSONB,
 false);

-- Report Template (Laudo Padrão Adulto)
INSERT INTO report_templates (id, name, description, blocks, is_default) VALUES
('62725c64-6aee-4d8e-9a1f-81bd391a8c1f', 'Laudo Padrão Adulto', 'Estrutura completa para avaliação neuropsicológica de adultos',
 '[
   {"id":"tpl-id","type":"identification","parentId":null,"order":0,"data":{}},
   {"id":"tpl-s1","type":"section","parentId":null,"order":1,"data":{"title":"DESCRIÇÃO DA DEMANDA E OBJETIVOS DA AVALIAÇÃO"}},
   {"id":"tpl-s1-t1","type":"text","parentId":"tpl-s1","order":0,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"tpl-s2","type":"section","parentId":null,"order":2,"data":{"title":"PROCEDIMENTOS"}},
   {"id":"tpl-s2-t1","type":"text","parentId":"tpl-s2","order":0,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"tpl-s3","type":"section","parentId":null,"order":3,"data":{"title":"ANAMNESE"}},
   {"id":"tpl-s3-t1","type":"text","parentId":"tpl-s3","order":0,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"tpl-s4","type":"section","parentId":null,"order":4,"data":{"title":"RESULTADOS"}},
   {"id":"tpl-s4-st1","type":"score-table","parentId":"tpl-s4","order":0,"data":{"title":"RESULTADOS - ATENÇÃO","columns":[{"id":"col-teste","label":"Teste"},{"id":"col-eb","label":"Escore Bruto"},{"id":"col-perc","label":"Percentil"},{"id":"col-class","label":"Classificação"}],"rows":[],"footnote":""}},
   {"id":"tpl-s4-st2","type":"score-table","parentId":"tpl-s4","order":1,"data":{"title":"RESULTADOS - MEMÓRIA","columns":[{"id":"col-teste","label":"Teste"},{"id":"col-eb","label":"Escore Bruto"},{"id":"col-perc","label":"Percentil"},{"id":"col-class","label":"Classificação"}],"rows":[],"footnote":""}},
   {"id":"tpl-s4-st3","type":"score-table","parentId":"tpl-s4","order":2,"data":{"title":"RESULTADOS - FUNÇÕES EXECUTIVAS","columns":[{"id":"col-teste","label":"Teste"},{"id":"col-eb","label":"Escore Bruto"},{"id":"col-perc","label":"Percentil"},{"id":"col-class","label":"Classificação"}],"rows":[],"footnote":""}},
   {"id":"tpl-s4-ch1","type":"chart","parentId":"tpl-s4","order":3,"data":{"title":"GRÁFICO DE DESEMPENHO","chartType":"bar","displayMode":"grouped","categories":[],"series":[],"referenceLines":[],"referenceRegions":[],"yAxisLabel":"","showValues":true,"showLegend":true,"showRegionLegend":false,"description":""}},
   {"id":"tpl-s5","type":"section","parentId":null,"order":5,"data":{"title":"ANÁLISE E OBSERVAÇÕES"}},
   {"id":"tpl-s5-t1","type":"text","parentId":"tpl-s5","order":0,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"tpl-s6","type":"section","parentId":null,"order":6,"data":{"title":"IMPRESSÃO DIAGNÓSTICA"}},
   {"id":"tpl-s6-ib1","type":"info-box","parentId":"tpl-s6","order":0,"data":{"label":"IMPRESSÃO DIAGNÓSTICA","content":""}},
   {"id":"tpl-s7","type":"section","parentId":null,"order":7,"data":{"title":"SUGESTÕES E ENCAMINHAMENTOS"}},
   {"id":"tpl-s7-t1","type":"text","parentId":"tpl-s7","order":0,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"tpl-s8","type":"section","parentId":null,"order":8,"data":{"title":"CONCLUSÃO"}},
   {"id":"tpl-s8-t1","type":"text","parentId":"tpl-s8","order":0,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"tpl-ref","type":"references","parentId":null,"order":9,"data":{"title":"REFERÊNCIAS BIBLIOGRÁFICAS","references":[]}},
   {"id":"tpl-cp","type":"closing-page","parentId":null,"order":10,"data":{"title":"TERMO DE ENTREGA E CIÊNCIA","bodyText":"","showPatientSignature":true,"showMotherSignature":false,"showFatherSignature":false,"showGuardianSignature":false,"footerNote":""}}
 ]'::JSONB, true);

-- Reports (3)
INSERT INTO reports (id, status, customer_name, customer_id, form_response_id, blocks) VALUES
('dddbef61-1bed-4352-b541-a0a2c05a01a9', 'FINALIZADO', 'Maria Aparecida Santos', 'b5e59243-c260-4ff4-891d-cdb97520d29d', 'e1ad1b5a-ad9c-4272-8d34-cc2b0dfb26a2',
 '[
   {"id":"b001-id","type":"identification","parentId":null,"order":0,"collapsed":false,"data":{"professional":{"name":"Dra. Ana Silva","crp":"06/12345","specialization":"Neuropsicologia"},"customer":{"name":"Maria Aparecida Santos","cpf":"983.535.674-23","birthDate":"1985-03-15","age":"40 anos","education":"Ensino Superior Completo","profession":"Professora","motherName":"Helena Maria dos Santos","fatherName":"Roberto Carlos Santos"},"date":"2025-12-15","location":"São Paulo - SP"}},
   {"id":"b001-s1","type":"section","parentId":null,"order":1,"collapsed":false,"data":{"title":"DESCRIÇÃO DA DEMANDA E OBJETIVOS DA AVALIAÇÃO"}},
   {"id":"b001-s1-t1","type":"text","parentId":"b001-s1","order":0,"collapsed":false,"data":{"content":[{"type":"p","children":[{"text":"Paciente do sexo feminino, 40 anos, encaminhada para avaliação neuropsicológica por queixa de ansiedade generalizada com episódios de insônia há aproximadamente 8 meses. Relata piora dos sintomas após mudança de emprego."}]}],"labeledItems":[],"useLabeledItems":false}},
   {"id":"b001-s2","type":"section","parentId":null,"order":2,"collapsed":false,"data":{"title":"PROCEDIMENTOS"}},
   {"id":"b001-s2-t1","type":"text","parentId":"b001-s2","order":0,"collapsed":false,"data":{"content":[{"type":"p","children":[{"text":"Foram realizadas 4 sessões de avaliação, incluindo entrevista clínica semiestruturada, aplicação do Inventário de Ansiedade de Beck (BAI) e Inventário de Depressão de Beck (BDI-II). Escore BAI: 28 (moderado). Escore BDI-II: 14 (leve)."}]}],"labeledItems":[],"useLabeledItems":false}},
   {"id":"b001-s3","type":"section","parentId":null,"order":3,"collapsed":false,"data":{"title":"RESULTADOS"}},
   {"id":"b001-s3-st1","type":"score-table","parentId":"b001-s3","order":0,"collapsed":false,"data":{"title":"RESULTADOS - ATENÇÃO","columns":[{"id":"col-teste","label":"Teste"},{"id":"col-eb","label":"Escore Bruto"},{"id":"col-perc","label":"Percentil"},{"id":"col-class","label":"Classificação"}],"rows":[{"id":"row-1","values":{"col-teste":"Trail Making A","col-eb":"35s","col-perc":"60","col-class":"Médio"}},{"id":"row-2","values":{"col-teste":"Trail Making B","col-eb":"78s","col-perc":"45","col-class":"Médio"}}],"footnote":""}},
   {"id":"b001-s3b","type":"section","parentId":null,"order":4,"collapsed":false,"data":{"title":"IMPRESSÃO DIAGNÓSTICA"}},
   {"id":"b001-ib1","type":"info-box","parentId":"b001-s3b","order":0,"collapsed":false,"data":{"label":"IMPRESSÃO DIAGNÓSTICA","content":[{"type":"p","children":[{"text":"Transtorno de Ansiedade Generalizada (F41.1) com impacto funcional moderado."}]}]}},
   {"id":"b001-s4","type":"section","parentId":null,"order":5,"collapsed":false,"data":{"title":"CONCLUSÃO E RECOMENDAÇÕES"}},
   {"id":"b001-s4-t1","type":"text","parentId":"b001-s4","order":0,"collapsed":false,"data":{"content":[{"type":"p","children":[{"text":"Recomenda-se acompanhamento psicoterapêutico semanal com abordagem cognitivo-comportamental por período mínimo de 12 semanas, com reavaliação ao final do período."}]}],"labeledItems":[],"useLabeledItems":false}},
   {"id":"b001-ref","type":"references","parentId":null,"order":6,"collapsed":false,"data":{"title":"REFERÊNCIAS BIBLIOGRÁFICAS","references":[]}},
   {"id":"b001-cp","type":"closing-page","parentId":null,"order":7,"collapsed":false,"data":{"title":"TERMO DE ENTREGA E CIÊNCIA","bodyText":"","showPatientSignature":true,"showMotherSignature":false,"showFatherSignature":false,"showGuardianSignature":false,"footerNote":""}}
 ]'::JSONB),

('fc2f985c-24c5-4c77-8288-49db8ef7e7e0', 'RASCUNHO', 'Maria Aparecida Santos', 'b5e59243-c260-4ff4-891d-cdb97520d29d', 'e1ad1b5a-ad9c-4272-8d34-cc2b0dfb26a2',
 '[
   {"id":"b003-id","type":"identification","parentId":null,"order":0,"collapsed":false,"data":{"professional":{"name":"Dra. Ana Silva","crp":"06/12345","specialization":"Neuropsicologia"},"customer":{"name":"Maria Aparecida Santos","cpf":"983.535.674-23","birthDate":"1985-03-15","age":"40 anos","education":"Ensino Superior Completo","profession":"Professora"},"date":"2026-03-22","location":"São Paulo - SP"}},
   {"id":"b003-s1","type":"section","parentId":null,"order":1,"collapsed":false,"data":{"title":"DESCRIÇÃO DA DEMANDA E OBJETIVOS DA AVALIAÇÃO"}},
   {"id":"b003-s1-t1","type":"text","parentId":"b003-s1","order":0,"collapsed":false,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"b003-s2","type":"section","parentId":null,"order":2,"collapsed":false,"data":{"title":"PROCEDIMENTOS"}},
   {"id":"b003-s2-t1","type":"text","parentId":"b003-s2","order":0,"collapsed":false,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"b003-s3","type":"section","parentId":null,"order":3,"collapsed":false,"data":{"title":"ANAMNESE"}},
   {"id":"b003-s3-t1","type":"text","parentId":"b003-s3","order":0,"collapsed":false,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"b003-s4","type":"section","parentId":null,"order":4,"collapsed":false,"data":{"title":"RESULTADOS"}},
   {"id":"b003-s4-st1","type":"score-table","parentId":"b003-s4","order":0,"collapsed":false,"data":{"title":"RESULTADOS - ATENÇÃO","columns":[{"id":"col-teste","label":"Teste"},{"id":"col-eb","label":"Escore Bruto"},{"id":"col-perc","label":"Percentil"},{"id":"col-class","label":"Classificação"}],"rows":[],"footnote":""}},
   {"id":"b003-s4b","type":"section","parentId":null,"order":5,"collapsed":false,"data":{"title":"IMPRESSÃO DIAGNÓSTICA"}},
   {"id":"b003-ib1","type":"info-box","parentId":"b003-s4b","order":0,"collapsed":false,"data":{"label":"IMPRESSÃO DIAGNÓSTICA","content":""}},
   {"id":"b003-s5","type":"section","parentId":null,"order":6,"collapsed":false,"data":{"title":"CONCLUSÃO"}},
   {"id":"b003-s5-t1","type":"text","parentId":"b003-s5","order":0,"collapsed":false,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"b003-cp","type":"closing-page","parentId":null,"order":7,"collapsed":false,"data":{"title":"TERMO DE ENTREGA E CIÊNCIA","bodyText":"","showPatientSignature":true,"showMotherSignature":false,"showFatherSignature":false,"showGuardianSignature":false,"footerNote":""}}
 ]'::JSONB),

('ef105901-94a7-46e6-8c7c-8f1dba6c20e5', 'RASCUNHO', 'José Carlos Oliveira', '46e24bd1-7cdd-4b84-b976-daaa11c1281b', NULL,
 '[
   {"id":"b002-id","type":"identification","parentId":null,"order":0,"collapsed":false,"data":{"professional":{"name":"Dra. Ana Silva","crp":"06/12345","specialization":"Neuropsicologia"},"customer":{"name":"José Carlos Oliveira","cpf":"093.646.184-59","birthDate":"1978-07-22","age":"47 anos","education":"Ensino Superior Completo","profession":"Engenheiro Civil"},"date":"2026-01-10","location":"São Paulo - SP"}},
   {"id":"b002-s1","type":"section","parentId":null,"order":1,"collapsed":false,"data":{"title":"DESCRIÇÃO DA DEMANDA"}},
   {"id":"b002-s1-t1","type":"text","parentId":"b002-s1","order":0,"collapsed":false,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"b002-s2","type":"section","parentId":null,"order":2,"collapsed":false,"data":{"title":"PROCEDIMENTOS"}},
   {"id":"b002-s2-t1","type":"text","parentId":"b002-s2","order":0,"collapsed":false,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"b002-s3","type":"section","parentId":null,"order":3,"collapsed":false,"data":{"title":"CONCLUSÃO"}},
   {"id":"b002-s3-t1","type":"text","parentId":"b002-s3","order":0,"collapsed":false,"data":{"content":"","labeledItems":[],"useLabeledItems":false}}
 ]'::JSONB);

-- Form (1 formulário com 2 versões)
INSERT INTO forms (id, current_major, current_minor) VALUES
('097b72f6-599c-4809-9753-0f0147eafa60', 2, 0);

INSERT INTO form_versions (id, form_id, version_major, version_minor, title, description, fields) VALUES
('1651322e-1949-40e2-9d01-926bb80d139b', '097b72f6-599c-4809-9753-0f0147eafa60', 1, 0,
 'Anamnese Neuropsicológica Adulto',
 'Formulário de anamnese para avaliação neuropsicológica de adultos',
 '[
   {"id":"queixa_principal","type":"long-text","label":"Qual a queixa principal?","required":true},
   {"id":"historico_sintomas","type":"long-text","label":"Há quanto tempo apresenta esses sintomas?","required":true},
   {"id":"tratamentos_anteriores","type":"single-choice","label":"Já realizou tratamento psicológico anteriormente?","options":[{"id":"opt-sim-01","label":"Sim"},{"id":"opt-nao-01","label":"Não"}],"required":true},
   {"id":"medicacao","type":"short-text","label":"Faz uso de alguma medicação? Qual?","required":false},
   {"id":"historico_familiar","type":"long-text","label":"Existe histórico de transtornos mentais na família?","required":true},
   {"id":"qualidade_sono","type":"single-choice","label":"Como avalia a qualidade do seu sono?","options":[{"id":"opt-sono-1","label":"Ótimo"},{"id":"opt-sono-2","label":"Bom"},{"id":"opt-sono-3","label":"Regular"},{"id":"opt-sono-4","label":"Ruim"},{"id":"opt-sono-5","label":"Péssimo"}],"required":true}
 ]'),

('a3b4c5d6-e7f8-4a9b-b0c1-d2e3f4a5b6c7', '097b72f6-599c-4809-9753-0f0147eafa60', 2, 0,
 'Anamnese Neuropsicológica Adulto',
 'Formulário de anamnese para avaliação neuropsicológica de adultos (v2)',
 '[
   {"id":"queixa_principal","type":"long-text","label":"Qual a queixa principal?","required":true},
   {"id":"historico_sintomas","type":"long-text","label":"Há quanto tempo apresenta esses sintomas?","required":true},
   {"id":"tratamentos_anteriores","type":"single-choice","label":"Já realizou tratamento psicológico anteriormente?","options":[{"id":"opt-sim-01","label":"Sim"},{"id":"opt-nao-01","label":"Não"}],"required":true},
   {"id":"medicacao","type":"short-text","label":"Faz uso de alguma medicação? Qual?","required":false},
   {"id":"historico_familiar","type":"long-text","label":"Existe histórico de transtornos mentais na família?","required":true},
   {"id":"qualidade_sono","type":"single-choice","label":"Como avalia a qualidade do seu sono?","options":[{"id":"opt-sono-1","label":"Ótimo"},{"id":"opt-sono-2","label":"Bom"},{"id":"opt-sono-3","label":"Regular"},{"id":"opt-sono-4","label":"Ruim"},{"id":"opt-sono-5","label":"Péssimo"}],"required":true},
   {"id":"atividade_fisica","type":"single-choice","label":"Pratica atividade física regularmente?","options":[{"id":"opt-af-1","label":"Sim, diariamente"},{"id":"opt-af-2","label":"Sim, algumas vezes por semana"},{"id":"opt-af-3","label":"Raramente"},{"id":"opt-af-4","label":"Não pratico"}],"required":true}
 ]');

-- Form responses (2)
INSERT INTO form_responses (id, form_id, form_version_id, customer_id, customer_name, status, generated_report_id, answers) VALUES
('e1ad1b5a-ad9c-4272-8d34-cc2b0dfb26a2', '097b72f6-599c-4809-9753-0f0147eafa60', 'a3b4c5d6-e7f8-4a9b-b0c1-d2e3f4a5b6c7',
 'b5e59243-c260-4ff4-891d-cdb97520d29d', 'Maria Aparecida Santos', 'CONCLUIDO', 'fc2f985c-24c5-4c77-8288-49db8ef7e7e0',
 '[
   {"fieldId":"queixa_principal","label":"Qual a queixa principal?","value":"Ansiedade intensa e crises de pânico frequentes nos últimos 8 meses"},
   {"fieldId":"historico_sintomas","label":"Há quanto tempo apresenta esses sintomas?","value":"Aproximadamente 8 meses, com piora após mudança de emprego"},
   {"fieldId":"tratamentos_anteriores","label":"Já realizou tratamento psicológico anteriormente?","selectedOptionIds":["opt-sim-01"],"value":"Sim"},
   {"fieldId":"medicacao","label":"Faz uso de alguma medicação? Qual?","value":"Escitalopram 10mg"},
   {"fieldId":"historico_familiar","label":"Existe histórico de transtornos mentais na família?","value":"Mãe com diagnóstico de depressão, avó materna com transtorno de ansiedade"},
   {"fieldId":"qualidade_sono","label":"Como avalia a qualidade do seu sono?","selectedOptionIds":["opt-sono-4"],"value":"Ruim"},
   {"fieldId":"atividade_fisica","label":"Pratica atividade física regularmente?","selectedOptionIds":["opt-af-3"],"value":"Raramente"}
 ]'::JSONB),

('f7a8b9c0-d1e2-4f3a-a4b5-c6d7e8f9a0b1', '097b72f6-599c-4809-9753-0f0147eafa60', 'a3b4c5d6-e7f8-4a9b-b0c1-d2e3f4a5b6c7',
 '9b687cc1-2aaa-423a-afbc-d2ac6ad244b1', 'Fernanda Rodrigues Lima', 'EM_ANDAMENTO', NULL,
 '[
   {"fieldId":"queixa_principal","label":"Qual a queixa principal?","value":"Episódios de tristeza profunda e falta de motivação há 6 meses"},
   {"fieldId":"historico_sintomas","label":"Há quanto tempo apresenta esses sintomas?","value":"6 meses, com agravamento nos últimos 2 meses"},
   {"fieldId":"tratamentos_anteriores","label":"Já realizou tratamento psicológico anteriormente?","selectedOptionIds":["opt-sim-01"],"value":"Sim"}
 ]'::JSONB);

-- Customer notes (Maria)
INSERT INTO customer_notes (id, customer_id, content, created_at, updated_at) VALUES
('c4d5e6f7-a8b9-4c0d-91e2-f3a4b5c6d7e8', 'b5e59243-c260-4ff4-891d-cdb97520d29d',
 'Paciente relata melhora na qualidade do sono após início do uso de melatonina. Mantém queixa de ansiedade em situações de avaliação no trabalho.',
 date_trunc('month', now()) - interval '30 days' + time '10:30',
 date_trunc('month', now()) - interval '30 days' + time '10:30'),

('d5e6f7a8-b9c0-4d1e-a2f3-b4c5d6e7f8a9', 'b5e59243-c260-4ff4-891d-cdb97520d29d',
 'Reavaliação com BAI: escore reduziu de 28 (moderado) para 16 (leve). Evolução positiva. Discutido possibilidade de redução para frequência quinzenal.',
 date_trunc('month', now()) - interval '10 days' + time '14:30',
 date_trunc('month', now()) - interval '10 days' + time '14:30');

-- Customer events (Maria)
INSERT INTO customer_events (id, customer_id, type, title, description, date, created_at) VALUES
('e6f7a8b9-c0d1-4e2f-b3a4-c5d6e7f8a9b0', 'b5e59243-c260-4ff4-891d-cdb97520d29d',
 'avaliacao', 'Avaliação psicológica inicial', 'Aplicados BAI (escore 28) e BDI-II (escore 14). Entrevista clínica realizada.',
 date_trunc('month', now()) - interval '30 days' + time '09:00',
 date_trunc('month', now()) - interval '30 days' + time '09:00'),

('f7a8b9c0-d1e2-4f3a-c4b5-d6e7f8a9b0c1', 'b5e59243-c260-4ff4-891d-cdb97520d29d',
 'laudo', 'Entrega do laudo', 'Devolutiva dos resultados e entrega do laudo.',
 date_trunc('month', now()) - interval '16 days' + time '10:00',
 date_trunc('month', now()) - interval '16 days' + time '10:00'),

('a8b9c0d1-e2f3-4a4b-d5c6-e7f8a9b0c1d2', 'b5e59243-c260-4ff4-891d-cdb97520d29d',
 'consulta', 'Início do acompanhamento', 'Primeira sessão de psicoterapia (TCC).',
 date_trunc('month', now()) + interval '1 day' + time '10:30',
 date_trunc('month', now()) + interval '1 day' + time '10:30');

-- Calendar tags
INSERT INTO event_tags (id, name, color) VALUES
('b9c0d1e2-f3a4-4b5c-e6d7-f8a9b0c1d2e3', 'Consulta', '#007AFF'),
('c0d1e2f3-a4b5-4c6d-f7e8-a9b0c1d2e3f4', 'Retorno', '#34C759'),
('d1e2f3a4-b5c6-4d7e-a8f9-b0c1d2e3f4a5', 'Avaliação', '#AF52DE'),
('e2f3a4b5-c6d7-4e8f-b9a0-c1d2e3f4a5b6', 'Supervisão', '#FF9500'),
('f3a4b5c6-d7e8-4f9a-c0b1-d2e3f4a5b6c7', 'Devolutiva', '#FF2D55'),
('a4b5c6d7-e8f9-4a0b-d1c2-e3f4a5b6c7d8', 'Atendimento', '#5856D6'),
('b5c6d7e8-f9a0-4b1c-e2d3-f4a5b6c7d8e9', 'Observação', '#8E8E93'),
('c6d7e8f9-a0b1-4c2d-f3e4-a5b6c7d8e9f0', 'Relatório Técnico', '#FF6B35'),
('d7e8f9a0-b1c2-4d3e-a4f5-b6c7d8e9f0a1', 'Laudo', '#30B0C7'),
('e8f9a0b1-c2d3-4e4f-b5a6-c7d8e9f0a1b2', 'Reunião', '#FFD60A'),
('f9a0b1c2-d3e4-4f5a-c6b7-d8e9f0a1b2c3', 'Encaminhamento', '#AC8E68');

-- Calendar events (3)
INSERT INTO calendar_events (id, summary, tag_id, customer_id, start_date_time, start_time_zone, end_date_time, end_time_zone, all_day, status) VALUES
('a0b1c2d3-e4f5-4a6b-d7c8-e9f0a1b2c3d4', 'Sessão com Maria',
 'b9c0d1e2-f3a4-4b5c-e6d7-f8a9b0c1d2e3', 'b5e59243-c260-4ff4-891d-cdb97520d29d',
 (date_trunc('month', now()) + interval '2 days' + time '09:00')::timestamptz, 'America/Sao_Paulo',
 (date_trunc('month', now()) + interval '2 days' + time '10:00')::timestamptz, 'America/Sao_Paulo', false, 'confirmed'),

('b1c2d3e4-f5a6-4b7c-e8d9-f0a1b2c3d4e5', 'Retorno Fernanda',
 'c0d1e2f3-a4b5-4c6d-f7e8-a9b0c1d2e3f4', '9b687cc1-2aaa-423a-afbc-d2ac6ad244b1',
 (date_trunc('month', now()) + interval '9 days' + time '14:00')::timestamptz, 'America/Sao_Paulo',
 (date_trunc('month', now()) + interval '9 days' + time '15:00')::timestamptz, 'America/Sao_Paulo', false, 'confirmed'),

('c2d3e4f5-a6b7-4c8d-f9e0-a1b2c3d4e5f6', 'Supervisão clínica',
 'e2f3a4b5-c6d7-4e8f-b9a0-c1d2e3f4a5b6', NULL,
 (date_trunc('month', now()) + interval '12 days' + time '16:00')::timestamptz, 'America/Sao_Paulo',
 (date_trunc('month', now()) + interval '12 days' + time '17:30')::timestamptz, 'America/Sao_Paulo', false, 'confirmed');

-- AI quota seed
INSERT INTO ai_quotas (id, ai_tier, model, monthly_limit, overage_price_cents, enabled) VALUES
('d3e4f5a6-b7c8-4d9e-a0f1-b2c3d4e5f6a7', 'DOX_IA', 'claude-sonnet-4-6', 15, 150, true);
