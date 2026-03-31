-- Professional Settings
INSERT INTO professional_settings (name, crp, specialization, phone, email, contact_items)
SELECT 'Dra. Ana Silva', '06/12345', 'Neuropsicologia', '(11) 98765-4321', 'ana.silva@clinica.com.br',
       '[{"type":"instagram","value":"@dra.anasilva"},{"type":"website","value":"www.clinicaanasilva.com.br"}]'::JSONB
WHERE NOT EXISTS (SELECT 1 FROM professional_settings);

-- Customers (3)
INSERT INTO customers (id, data, deleted) VALUES
('c0000001-0000-0000-0000-000000000001',
 '{"name":"Maria Aparecida Santos","cpf":"111.222.333-44","birthDate":"1985-03-15","age":"40 anos","education":"Ensino Superior Completo","profession":"Professora","motherName":"Helena Maria dos Santos","fatherName":"Roberto Carlos Santos","phone":"(11) 99999-0001","email":"maria.santos@email.com","addressStreet":"Rua das Flores, 100","addressCity":"São Paulo","addressState":"SP","addressZipCode":"01234-567","chiefComplaint":"Ansiedade generalizada com episódios de insônia há 8 meses","diagnosis":"Transtorno de Ansiedade Generalizada (F41.1)","medications":"Escitalopram 10mg","referralDoctor":"Dr. Paulo Henrique Mendes - CRM/SP 123456"}'::JSONB,
 false),

('c0000002-0000-0000-0000-000000000002',
 '{"name":"José Carlos Oliveira","cpf":"222.333.444-55","birthDate":"1978-07-22","email":"jose.oliveira@email.com","phone":"(11) 99999-0002","address":"Av. Paulista, 1500 - São Paulo/SP"}'::JSONB,
 false),

('c0000003-0000-0000-0000-000000000003',
 '{"name":"Fernanda Rodrigues Lima","cpf":"333.444.555-66","birthDate":"1992-11-08","email":"fernanda.lima@email.com","phone":"(21) 99999-0003","address":"Rua Copacabana, 45 - Rio de Janeiro/RJ"}'::JSONB,
 false);

-- Report Template (Laudo Padrão Adulto) — com section type + parentId
INSERT INTO report_templates (id, name, description, blocks, is_default) VALUES
('de000001-0000-0000-0000-000000000001', 'Laudo Padrão Adulto', 'Estrutura completa para avaliação de adultos',
 '[
   {"id":"tpl-id","type":"identification","parentId":null,"order":0,"data":{}},
   {"id":"tpl-s1","type":"section","parentId":null,"order":1,"data":{"title":"DESCRIÇÃO DA DEMANDA E OBJETIVOS DA AVALIAÇÃO"}},
   {"id":"tpl-s1-t1","type":"text","parentId":"tpl-s1","order":0,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"tpl-s2","type":"section","parentId":null,"order":2,"data":{"title":"PROCEDIMENTOS"}},
   {"id":"tpl-s2-t1","type":"text","parentId":"tpl-s2","order":0,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"tpl-s3","type":"section","parentId":null,"order":3,"data":{"title":"ANAMNESE"}},
   {"id":"tpl-s3-t1","type":"text","parentId":"tpl-s3","order":0,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"tpl-s4","type":"section","parentId":null,"order":4,"data":{"title":"RESULTADOS"}},
   {"id":"tpl-s4-st1","type":"score-table","parentId":"tpl-s4","order":0,"data":{"title":"RESULTADOS - ATENÇÃO","columns":["Teste","Escore Bruto","Percentil","Classificação"],"rows":[],"formulas":{},"columnAlignments":{}}},
   {"id":"tpl-s4-st2","type":"score-table","parentId":"tpl-s4","order":1,"data":{"title":"RESULTADOS - MEMÓRIA","columns":["Teste","Escore Bruto","Percentil","Classificação"],"rows":[],"formulas":{},"columnAlignments":{}}},
   {"id":"tpl-s4-st3","type":"score-table","parentId":"tpl-s4","order":2,"data":{"title":"RESULTADOS - FUNÇÕES EXECUTIVAS","columns":["Teste","Escore Bruto","Percentil","Classificação"],"rows":[],"formulas":{},"columnAlignments":{}}},
   {"id":"tpl-s4-ch1","type":"chart","parentId":"tpl-s4","order":3,"data":{"title":"GRÁFICO DE DESEMPENHO","chartType":"bar","categories":[],"series":[]}},
   {"id":"tpl-s5","type":"section","parentId":null,"order":5,"data":{"title":"ANÁLISE E OBSERVAÇÕES"}},
   {"id":"tpl-s5-t1","type":"text","parentId":"tpl-s5","order":0,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"tpl-s6-ib1","type":"info-box","parentId":null,"order":6,"data":{"label":"IMPRESSÃO DIAGNÓSTICA","content":""}},
   {"id":"tpl-s7","type":"section","parentId":null,"order":7,"data":{"title":"SUGESTÕES E ENCAMINHAMENTOS"}},
   {"id":"tpl-s7-t1","type":"text","parentId":"tpl-s7","order":0,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"tpl-s8","type":"section","parentId":null,"order":8,"data":{"title":"CONCLUSÃO"}},
   {"id":"tpl-s8-t1","type":"text","parentId":"tpl-s8","order":0,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"tpl-ref","type":"references","parentId":null,"order":9,"data":{"title":"REFERÊNCIAS BIBLIOGRÁFICAS","references":["Wechsler, D. (2017). Escala de Inteligência Wechsler para Adultos 3ª. Edição – WAIS III. São Paulo: Pearson.","D''Paula, J. J. & Malloy-Diniz, L. F. (2018). Teste de Aprendizagem Auditivo-Verbal de Rey – RAVLT. São Paulo: Vetor.","Rey, A. (2014). Figuras Complexas de Rey – Teste de Cópia e de Reprodução de Memória de Figuras Geométricas Complexas. 2ª ed., São Paulo: Pearson.","Rueda, F. J. M. (2022). Bateria Psicológica para Avaliação da Atenção – BPA-2. São Paulo: Vetor.","Sedó, M.; Paula, J. J.; Malloy-Diniz, L.F. (2015). O teste dos cinco dígitos, FDT – Five Digit Test, São Paulo: Hogrefe CETEPP.","Alves, Lemes e Rabelo (2019). Inventário fatorial de personalidade – IFP-II. Editora Pearson.","Benczik, E. B. P. (2013). Escala de Avaliação de Transtorno de Déficit de Atenção e Hiperatividade – Versão Adolescentes e Adultos – ETDAH-AD. 1ª ed. São Paulo: Vetor.","Gruber, C. P. & Constantino, J. N. (2021). Escala de Responsividade Social/SRS-2. São Paulo: Hogrefe CETEPP.","Serpa, A; Timóteo, A; Oliveira, R; Querino, E; e Malloy-Diniz, L.F. (2023). Torre de Londres – ToL-BR, São Paulo: Vetor.","Pessotto, F. & Bartholomeu, D. (2019). Guia prático das Escalas Wechsler: uso e análise das escalas WISCIV, WAIS-III e WASI. São Paulo: Pearson Clinical Brasil.","Malloy-Diniz, L. F.; De Paula, J. J.; Sedó, M.; Fuentes, D. & Leite, W. B. (2014). Neuropsicologia das funções executivas e da atenção. In D. Fuentes; L. F. Malloy-Diniz; C. H. P. Camargo & R. M. Cosenza (Orgs). Neuropsicologia: Teoria e Prática. 2 ed. Porto Alegre: Artmed.","Brião, J. C. & Campanholo, K. R. (2018). Funções executivas. In E. C. Miotto., K. R. Campanholo, V. T. Serrao & B. T. Trevisan. Vol. 1. Manual de Avaliação Neuropsicológica. São Paulo: Memnon.","Diamond, A. (2013). Executive functions. Annu Rev Psychol, 64, 135-168.","Leandro F. Malloy-Diniz ... [et al.]. (2010). Avaliação neuropsicológica. Porto Alegre: Artmed.","Cardoso, C. O.; Pureza, J. R.; Gonçalves, H. A.; Jacobsen, G.; Senger, J.; Colling, A. P. C. & Fonseca, R. P. (2015). Funções executivas e regulação emocional: intervenções e implicações educacionais. In N. M. Dias & T. Mecca. Contribuições da neuropsicologia e da psicologia para intervenção no contexto educacional. São Paulo: Memnon."]}},
   {"id":"tpl-cp","type":"closing-page","parentId":null,"order":10,"data":{"title":"TERMO DE ENTREGA E CIÊNCIA","signatures":["professional","patient"]}}
 ]'::JSONB, true);

-- Reports (2) — com section type + parentId
INSERT INTO reports (id, status, customer_name, customer_id, form_response_id, blocks) VALUES
('d0000001-0000-0000-0000-000000000001', 'FINALIZADO', 'Maria Aparecida Santos', 'c0000001-0000-0000-0000-000000000001', 'fa000001-0000-0000-0000-000000000001',
 '[
   {"id":"b001-0001","type":"identification","parentId":null,"order":0,"collapsed":false,"data":{"professional":{"name":"Dra. Ana Silva","crp":"06/12345","specialization":"Neuropsicologia"},"customer":{"name":"Maria Aparecida Santos","cpf":"111.222.333-44","birthDate":"1985-03-15","age":"40 anos","education":"Ensino Superior Completo","profession":"Professora","motherName":"Helena Maria dos Santos","fatherName":"Roberto Carlos Santos"},"date":"2025-12-15","location":"São Paulo - SP"}},
   {"id":"b001-0002","type":"section","parentId":null,"order":1,"collapsed":false,"data":{"title":"DESCRIÇÃO DA DEMANDA E OBJETIVOS DA AVALIAÇÃO"}},
   {"id":"b001-0002-t1","type":"text","parentId":"b001-0002","order":0,"collapsed":false,"data":{"content":[{"type":"paragraph","children":[{"text":"Paciente do sexo feminino, 40 anos, encaminhada para avaliação neuropsicológica por queixa de ansiedade generalizada com episódios de insônia há aproximadamente 8 meses. Relata piora dos sintomas após mudança de emprego."}]}],"labeledItems":[],"useLabeledItems":false}},
   {"id":"b001-0003","type":"section","parentId":null,"order":2,"collapsed":false,"data":{"title":"PROCEDIMENTOS"}},
   {"id":"b001-0003-t1","type":"text","parentId":"b001-0003","order":0,"collapsed":false,"data":{"content":[{"type":"paragraph","children":[{"text":"Foram realizadas 4 sessões de avaliação, incluindo entrevista clínica semiestruturada, aplicação do Inventário de Ansiedade de Beck (BAI) e Inventário de Depressão de Beck (BDI-II). Escore BAI: 28 (moderado). Escore BDI-II: 14 (leve)."}]}],"labeledItems":[],"useLabeledItems":false}},
   {"id":"b001-0004","type":"section","parentId":null,"order":3,"collapsed":false,"data":{"title":"RESULTADOS"}},
   {"id":"b001-0004-st1","type":"score-table","parentId":"b001-0004","order":0,"collapsed":false,"data":{"title":"RESULTADOS - ATENÇÃO","columns":["Teste","Escore Bruto","Percentil","Classificação"],"rows":[["Trail Making A","35s","60","Médio"],["Trail Making B","78s","45","Médio"]],"formulas":{},"columnAlignments":{}}},
   {"id":"b001-0005","type":"info-box","parentId":null,"order":4,"collapsed":false,"data":{"label":"IMPRESSÃO DIAGNÓSTICA","content":[{"type":"paragraph","children":[{"text":"Transtorno de Ansiedade Generalizada (F41.1) com impacto funcional moderado."}]}]}},
   {"id":"b001-0006","type":"section","parentId":null,"order":5,"collapsed":false,"data":{"title":"CONCLUSÃO E RECOMENDAÇÕES"}},
   {"id":"b001-0006-t1","type":"text","parentId":"b001-0006","order":0,"collapsed":false,"data":{"content":[{"type":"paragraph","children":[{"text":"Recomenda-se acompanhamento psicoterapêutico semanal com abordagem cognitivo-comportamental por período mínimo de 12 semanas, com reavaliação ao final do período."}]}],"labeledItems":[],"useLabeledItems":false}},
   {"id":"b001-0007","type":"references","parentId":null,"order":6,"collapsed":false,"data":{"title":"REFERÊNCIAS BIBLIOGRÁFICAS","references":["Wechsler, D. (2017). Escala de Inteligência Wechsler para Adultos 3ª. Edição – WAIS III. São Paulo: Pearson.","D''Paula, J. J. & Malloy-Diniz, L. F. (2018). Teste de Aprendizagem Auditivo-Verbal de Rey – RAVLT. São Paulo: Vetor.","Rey, A. (2014). Figuras Complexas de Rey – Teste de Cópia e de Reprodução de Memória de Figuras Geométricas Complexas. 2ª ed., São Paulo: Pearson.","Sedó, M.; Paula, J. J.; Malloy-Diniz, L.F. (2015). O teste dos cinco dígitos, FDT – Five Digit Test, São Paulo: Hogrefe CETEPP.","Benczik, E. B. P. (2013). Escala de Avaliação de Transtorno de Déficit de Atenção e Hiperatividade – ETDAH-AD. 1ª ed. São Paulo: Vetor.","Gruber, C. P. & Constantino, J. N. (2021). Escala de Responsividade Social/SRS-2. São Paulo: Hogrefe CETEPP."]}},
   {"id":"b001-0008","type":"closing-page","parentId":null,"order":7,"collapsed":false,"data":{"title":"TERMO DE ENTREGA E CIÊNCIA","signatures":["professional","patient"]}}
 ]'::JSONB),

('d0000003-0000-0000-0000-000000000003', 'RASCUNHO', 'Maria Aparecida Santos', 'c0000001-0000-0000-0000-000000000001', 'fa000001-0000-0000-0000-000000000001',
 '[
   {"id":"b003-0001","type":"identification","parentId":null,"order":0,"collapsed":false,"data":{"professional":{"name":"Dra. Ana Silva","crp":"06/12345","specialization":"Neuropsicologia"},"customer":{"name":"Maria Aparecida Santos","cpf":"111.222.333-44","birthDate":"1985-03-15","age":"40 anos","education":"Ensino Superior Completo","profession":"Professora"},"date":"2026-03-22","location":"São Paulo - SP"}},
   {"id":"b003-0002","type":"section","parentId":null,"order":1,"collapsed":false,"data":{"title":"DESCRIÇÃO DA DEMANDA E OBJETIVOS DA AVALIAÇÃO"}},
   {"id":"b003-0002-t1","type":"text","parentId":"b003-0002","order":0,"collapsed":false,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"b003-0003","type":"section","parentId":null,"order":2,"collapsed":false,"data":{"title":"PROCEDIMENTOS"}},
   {"id":"b003-0003-t1","type":"text","parentId":"b003-0003","order":0,"collapsed":false,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"b003-0004","type":"section","parentId":null,"order":3,"collapsed":false,"data":{"title":"ANAMNESE"}},
   {"id":"b003-0004-t1","type":"text","parentId":"b003-0004","order":0,"collapsed":false,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"b003-0005","type":"section","parentId":null,"order":4,"collapsed":false,"data":{"title":"RESULTADOS"}},
   {"id":"b003-0005-st1","type":"score-table","parentId":"b003-0005","order":0,"collapsed":false,"data":{"title":"RESULTADOS - ATENÇÃO","columns":["Teste","Escore Bruto","Percentil","Classificação"],"rows":[],"formulas":{},"columnAlignments":{}}},
   {"id":"b003-0006","type":"info-box","parentId":null,"order":5,"collapsed":false,"data":{"label":"IMPRESSÃO DIAGNÓSTICA","content":""}},
   {"id":"b003-0007","type":"section","parentId":null,"order":6,"collapsed":false,"data":{"title":"CONCLUSÃO"}},
   {"id":"b003-0007-t1","type":"text","parentId":"b003-0007","order":0,"collapsed":false,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"b003-0008","type":"closing-page","parentId":null,"order":7,"collapsed":false,"data":{"title":"TERMO DE ENTREGA E CIÊNCIA","signatures":["professional","patient"]}}
 ]'::JSONB),

('d0000002-0000-0000-0000-000000000002', 'RASCUNHO', 'José Carlos Oliveira', 'c0000002-0000-0000-0000-000000000002', NULL,
 '[
   {"id":"b002-0001","type":"identification","parentId":null,"order":0,"collapsed":false,"data":{"professional":{"name":"Dra. Ana Silva","crp":"06/12345","specialization":"Neuropsicologia"},"customer":{"name":"José Carlos Oliveira","cpf":"222.333.444-55","birthDate":"1978-07-22","age":"47 anos","education":"Ensino Superior Completo","profession":"Engenheiro Civil"},"date":"2026-01-10","location":"São Paulo - SP"}},
   {"id":"b002-0002","type":"section","parentId":null,"order":1,"collapsed":false,"data":{"title":"DESCRIÇÃO DA DEMANDA"}},
   {"id":"b002-0002-t1","type":"text","parentId":"b002-0002","order":0,"collapsed":false,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"b002-0003","type":"section","parentId":null,"order":2,"collapsed":false,"data":{"title":"PROCEDIMENTOS"}},
   {"id":"b002-0003-t1","type":"text","parentId":"b002-0003","order":0,"collapsed":false,"data":{"content":"","labeledItems":[],"useLabeledItems":false}},
   {"id":"b002-0004","type":"section","parentId":null,"order":3,"collapsed":false,"data":{"title":"CONCLUSÃO"}},
   {"id":"b002-0004-t1","type":"text","parentId":"b002-0004","order":0,"collapsed":false,"data":{"content":"","labeledItems":[],"useLabeledItems":false}}
 ]'::JSONB);

-- Form (1 formulário com 2 versões, vinculado ao template)
INSERT INTO forms (id, current_version, linked_template_id) VALUES
('e0000001-0000-0000-0000-000000000001', 2, 'de000001-0000-0000-0000-000000000001');

INSERT INTO form_versions (id, form_id, version, title, description, fields) VALUES
('f0000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000001', 1,
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

('f0000002-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000001', 2,
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
('fa000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000001', 'f0000002-0000-0000-0000-000000000001',
 'c0000001-0000-0000-0000-000000000001', 'Maria Aparecida Santos', 'CONCLUIDO', 'd0000003-0000-0000-0000-000000000003',
 '[
   {"fieldId":"queixa_principal","value":"Ansiedade intensa e crises de pânico frequentes nos últimos 8 meses"},
   {"fieldId":"historico_sintomas","value":"Aproximadamente 8 meses, com piora após mudança de emprego"},
   {"fieldId":"tratamentos_anteriores","selectedOptionIds":["opt-sim-01"]},
   {"fieldId":"medicacao","value":"Escitalopram 10mg"},
   {"fieldId":"historico_familiar","value":"Mãe com diagnóstico de depressão, avó materna com transtorno de ansiedade"},
   {"fieldId":"qualidade_sono","selectedOptionIds":["opt-sono-4"]},
   {"fieldId":"atividade_fisica","selectedOptionIds":["opt-af-3"]}
 ]'::JSONB),

('fa000002-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000001', 'f0000002-0000-0000-0000-000000000001',
 'c0000003-0000-0000-0000-000000000003', 'Fernanda Rodrigues Lima', 'EM_ANDAMENTO', NULL,
 '[
   {"fieldId":"queixa_principal","value":"Episódios de tristeza profunda e falta de motivação"},
   {"fieldId":"historico_sintomas","value":"6 meses"},
   {"fieldId":"tratamentos_anteriores","selectedOptionIds":["opt-sim-01"]}
 ]'::JSONB);

-- Customer notes (Maria)
INSERT INTO customer_notes (id, customer_id, content, created_at, updated_at) VALUES
('ca000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000001',
 'Paciente relata melhora na qualidade do sono após início do uso de melatonina. Mantém queixa de ansiedade em situações de avaliação no trabalho.',
 date_trunc('month', now()) - interval '30 days' + time '10:30',
 date_trunc('month', now()) - interval '30 days' + time '10:30'),

('ca000002-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000001',
 'Reavaliação com BAI: escore reduziu de 28 (moderado) para 16 (leve). Evolução positiva. Discutido possibilidade de redução para frequência quinzenal.',
 date_trunc('month', now()) - interval '10 days' + time '14:30',
 date_trunc('month', now()) - interval '10 days' + time '14:30');

-- Customer events (Maria)
INSERT INTO customer_events (id, customer_id, type, title, description, date, created_at) VALUES
('cb000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000001',
 'avaliacao', 'Avaliação psicológica inicial', 'Aplicados BAI (escore 28) e BDI-II (escore 14). Entrevista clínica realizada.',
 date_trunc('month', now()) - interval '30 days' + time '09:00',
 date_trunc('month', now()) - interval '30 days' + time '09:00'),

('cb000002-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000001',
 'laudo', 'Entrega do laudo', 'Devolutiva dos resultados e entrega do laudo.',
 date_trunc('month', now()) - interval '16 days' + time '10:00',
 date_trunc('month', now()) - interval '16 days' + time '10:00'),

('cb000003-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000001',
 'consulta', 'Início do acompanhamento', 'Primeira sessão de psicoterapia (TCC).',
 date_trunc('month', now()) + interval '1 day' + time '10:30',
 date_trunc('month', now()) + interval '1 day' + time '10:30');

-- Calendar tags
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

-- Calendar events (3)
INSERT INTO calendar_events (id, summary, tag_id, customer_id, start_date_time, start_time_zone, end_date_time, end_time_zone, all_day, status) VALUES
('c1e00001-0000-4000-8000-000000000001', 'Sessão com Maria',
 'e1a00001-0000-4000-8000-000000000001', 'c0000001-0000-0000-0000-000000000001',
 (date_trunc('month', now()) + interval '2 days' + time '09:00')::timestamptz, 'America/Sao_Paulo',
 (date_trunc('month', now()) + interval '2 days' + time '10:00')::timestamptz, 'America/Sao_Paulo', false, 'confirmed'),

('c1e00002-0000-4000-8000-000000000002', 'Retorno Fernanda',
 'e1a00002-0000-4000-8000-000000000002', 'c0000003-0000-0000-0000-000000000003',
 (date_trunc('month', now()) + interval '9 days' + time '14:00')::timestamptz, 'America/Sao_Paulo',
 (date_trunc('month', now()) + interval '9 days' + time '15:00')::timestamptz, 'America/Sao_Paulo', false, 'confirmed'),

('c1e00003-0000-4000-8000-000000000003', 'Supervisão clínica',
 'e1a00004-0000-4000-8000-000000000004', NULL,
 (date_trunc('month', now()) + interval '12 days' + time '16:00')::timestamptz, 'America/Sao_Paulo',
 (date_trunc('month', now()) + interval '12 days' + time '17:30')::timestamptz, 'America/Sao_Paulo', false, 'confirmed');

-- AI quota seed
INSERT INTO ai_quotas (id, ai_tier, model, monthly_limit, overage_price_cents, enabled) VALUES
('a0000001-0000-0000-0000-000000000001', 'DOX_IA', 'claude-sonnet-4-6', 15, 150, true);
