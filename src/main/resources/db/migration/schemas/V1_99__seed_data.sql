INSERT INTO professional_settings (name, crp, specialization, phone, email, contact_items)
SELECT 'Dra. Ana Silva', '06/12345', 'Psicologia Clínica', '(11) 98765-4321', 'ana.silva@clinica.com.br',
       '[{"id":"ct-001","type":"instagram","value":"@dra.anasilva"},{"id":"ct-002","type":"email","value":"ana.silva@clinica.com.br"}]'::JSONB
WHERE NOT EXISTS (SELECT 1 FROM professional_settings);

INSERT INTO customers (id, data, deleted) VALUES
('3f8a1b2c-4d5e-6f7a-8b9c-0d1e2f3a4b5c', '{
  "name": "Maria Aparecida Santos",
  "sex": "Feminino",
  "cpf": "111.222.333-44",
  "email": "maria.santos@email.com",
  "phone": "(11) 99999-0001",
  "birthDate": "1985-03-15",
  "age": "40 anos",
  "education": "Ensino Superior Completo",
  "profession": "Professora",
  "motherName": "Helena Santos",
  "fatherName": "Roberto Santos",
  "address": "Rua das Flores, 100 - São Paulo/SP"
}', false),
('7e2d9c4a-1b3f-5a6e-8d0c-2f4b6a8e0c1d', '{
  "name": "José Carlos Oliveira",
  "sex": "Masculino",
  "cpf": "222.333.444-55",
  "email": "jose.oliveira@email.com",
  "phone": "(11) 99999-0002",
  "birthDate": "1978-07-22",
  "age": "47 anos",
  "education": "Ensino Superior Completo",
  "profession": "Engenheiro Civil",
  "motherName": "Francisca Oliveira",
  "fatherName": "Antônio Carlos Oliveira",
  "address": "Av. Paulista, 1500 - São Paulo/SP"
}', false),
('a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', '{
  "name": "Fernanda Rodrigues Lima",
  "sex": "Feminino",
  "cpf": "333.444.555-66",
  "email": "fernanda.lima@email.com",
  "phone": "(21) 99999-0003",
  "birthDate": "1992-11-08",
  "age": "33 anos",
  "education": "Ensino Superior Completo",
  "profession": "Designer Gráfica",
  "motherName": "Regina Lima",
  "fatherName": "Carlos Rodrigues Lima",
  "address": "Rua Copacabana, 45 - Rio de Janeiro/RJ"
}', false);

INSERT INTO report_templates (id, name, description, is_default, blocks) VALUES
('b4c5d6e7-f8a9-0b1c-2d3e-4f5a6b7c8d9e',
 'Laudo Psicológico — Resolução CFP 06/2019',
 'Template baseado na Resolução CFP nº 06/2019 que regulamenta a elaboração de documentos escritos produzidos pela(o) psicóloga(o)',
 true,
 '[
   {
     "id": "tpl-blk-001",
     "type": "identification",
     "order": 0,
     "collapsed": false,
     "data": {
       "professional": {"name": "", "crp": "", "specialization": ""},
       "customer": {"name": "", "cpf": "", "birthDate": "", "age": "", "education": "", "profession": "", "motherName": "", "fatherName": ""},
       "date": "",
       "location": ""
     }
   },
   {
     "id": "tpl-blk-002",
     "type": "text",
     "order": 1,
     "collapsed": false,
     "data": {
       "title": "DESCRIÇÃO DA DEMANDA",
       "subtitle": "",
       "content": "",
       "labeledItems": [],
       "useLabeledItems": false
     }
   },
   {
     "id": "tpl-blk-003",
     "type": "text",
     "order": 2,
     "collapsed": false,
     "data": {
       "title": "PROCEDIMENTOS",
       "subtitle": "Instrumentos e técnicas utilizadas",
       "content": "",
       "labeledItems": [],
       "useLabeledItems": false
     }
   },
   {
     "id": "tpl-blk-004",
     "type": "text",
     "order": 3,
     "collapsed": false,
     "data": {
       "title": "ANÁLISE",
       "subtitle": "Fundamentação teórica e interpretação dos dados",
       "content": "",
       "labeledItems": [],
       "useLabeledItems": false
     }
   },
   {
     "id": "tpl-blk-005",
     "type": "text",
     "order": 4,
     "collapsed": false,
     "data": {
       "title": "CONCLUSÃO",
       "subtitle": "",
       "content": "",
       "labeledItems": [],
       "useLabeledItems": false
     }
   },
   {
     "id": "tpl-blk-006",
     "type": "info-box",
     "order": 5,
     "collapsed": false,
     "data": {
       "label": "Indicação Terapêutica",
       "content": ""
     }
   },
   {
     "id": "tpl-blk-007",
     "type": "references",
     "order": 6,
     "collapsed": false,
     "data": {
       "title": "REFERÊNCIAS",
       "references": [""]
     }
   }
 ]'::JSONB);

INSERT INTO forms (id, linked_template_id, current_version) VALUES
('c8d9e0f1-a2b3-4c5d-6e7f-8a9b0c1d2e3f', 'b4c5d6e7-f8a9-0b1c-2d3e-4f5a6b7c8d9e', 1);

INSERT INTO form_versions (id, form_id, version, title, description, fields) VALUES
('d9e0f1a2-b3c4-5d6e-7f8a-9b0c1d2e3f4a', 'c8d9e0f1-a2b3-4c5d-6e7f-8a9b0c1d2e3f', 1,
 'Triagem para Avaliação de TEA',
 'Questionário de triagem para Transtorno do Espectro Autista — coleta inicial de informações para laudo psicológico',
 '[
   {"id": "secao_dados", "type": "section-header", "label": "Dados do Paciente"},
   {"id": "sexo", "type": "single-choice", "label": "Sexo", "options": [{"id": "sex-m", "label": "Masculino"}, {"id": "sex-f", "label": "Feminino"}, {"id": "sex-o", "label": "Outro"}], "required": true},
   {"id": "idade_atual", "type": "short-text", "label": "Idade atual", "required": true, "variableKey": "idade"},
   {"id": "queixa_principal", "type": "long-text", "label": "Qual a queixa principal que motivou a busca pela avaliação?", "required": true},
   {"id": "quem_encaminhou", "type": "single-choice", "label": "Quem encaminhou para avaliação?", "options": [{"id": "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", "label": "Escola"}, {"id": "2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d7e", "label": "Neuropediatra"}, {"id": "3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f", "label": "Pediatra"}, {"id": "4d5e6f7a-8b9c-0d1e-2f3a-4b5c6d7e8f9a", "label": "Fonoaudióloga"}, {"id": "5e6f7a8b-9c0d-1e2f-3a4b-5c6d7e8f9a0b", "label": "Demanda espontânea"}], "required": true},

   {"id": "secao_desenvolvimento", "type": "section-header", "label": "Desenvolvimento"},
   {"id": "idade_primeiras_palavras", "type": "short-text", "label": "Com que idade falou as primeiras palavras?", "required": false},
   {"id": "idade_andou", "type": "short-text", "label": "Com que idade começou a andar?", "required": false},
   {"id": "regressao", "type": "single-choice", "label": "Houve perda de habilidades já adquiridas (regressão)?", "options": [{"id": "6f7a8b9c-0d1e-2f3a-4b5c-6d7e8f9a0b1c", "label": "Sim"}, {"id": "7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d", "label": "Não"}, {"id": "8b9c0d1e-2f3a-4b5c-6d7e-8f9a0b1c2d3e", "label": "Não sei informar"}], "required": true},

   {"id": "secao_comunicacao", "type": "section-header", "label": "Comunicação e Interação Social"},
   {"id": "contato_visual", "type": "single-choice", "label": "Como é o contato visual?", "options": [{"id": "9c0d1e2f-3a4b-5c6d-7e8f-9a0b1c2d3e4f", "label": "Adequado"}, {"id": "0d1e2f3a-4b5c-6d7e-8f9a-0b1c2d3e4f5a", "label": "Breve / inconsistente"}, {"id": "1e2f3a4b-5c6d-7e8f-9a0b-1c2d3e4f5a6b", "label": "Ausente ou muito raro"}], "required": true},
   {"id": "responde_nome", "type": "single-choice", "label": "Responde quando chamado pelo nome?", "options": [{"id": "2f3a4b5c-6d7e-8f9a-0b1c-2d3e4f5a6b7c", "label": "Sempre"}, {"id": "3a4b5c6d-7e8f-9a0b-1c2d-3e4f5a6b7c8d", "label": "Às vezes"}, {"id": "4b5c6d7e-8f9a-0b1c-2d3e-4f5a6b7c8d9e", "label": "Raramente ou nunca"}], "required": true},
   {"id": "interesse_criancas", "type": "single-choice", "label": "Demonstra interesse em brincar com outras crianças?", "options": [{"id": "5c6d7e8f-9a0b-1c2d-3e4f-5a6b7c8d9e0f", "label": "Sim"}, {"id": "6d7e8f9a-0b1c-2d3e-4f5a-6b7c8d9e0f1a", "label": "Pouco"}, {"id": "7e8f9a0b-1c2d-3e4f-5a6b-7c8d9e0f1a2b", "label": "Não"}], "required": true},
   {"id": "aponta_objetos", "type": "single-choice", "label": "Aponta para mostrar ou pedir coisas?", "options": [{"id": "8f9a0b1c-2d3e-4f5a-6b7c-8d9e0f1a2b3c", "label": "Sim"}, {"id": "9a0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d", "label": "Raramente"}, {"id": "0b1c2d3e-4f5a-6b7c-8d9e-0f1a2b3c4d5e", "label": "Não"}], "required": true},

   {"id": "secao_comportamento", "type": "section-header", "label": "Padrões de Comportamento"},
   {"id": "interesses_restritos", "type": "long-text", "label": "Possui interesses muito específicos ou restritos? Quais?", "required": false},
   {"id": "rotinas_rigidas", "type": "single-choice", "label": "Apresenta rigidez com rotinas (fica muito incomodado com mudanças)?", "options": [{"id": "1c2d3e4f-5a6b-7c8d-9e0f-1a2b3c4d5e6f", "label": "Sim, muito"}, {"id": "2d3e4f5a-6b7c-8d9e-0f1a-2b3c4d5e6f7a", "label": "Um pouco"}, {"id": "3e4f5a6b-7c8d-9e0f-1a2b-3c4d5e6f7a8b", "label": "Não"}], "required": true},
   {"id": "estereotipias", "type": "multiple-choice", "label": "Apresenta movimentos repetitivos (estereotipias)?", "options": [{"id": "4f5a6b7c-8d9e-0f1a-2b3c-4d5e6f7a8b9c", "label": "Balançar as mãos (flapping)"}, {"id": "5a6b7c8d-9e0f-1a2b-3c4d-5e6f7a8b9c0d", "label": "Girar objetos"}, {"id": "6b7c8d9e-0f1a-2b3c-4d5e-6f7a8b9c0d1e", "label": "Balançar o corpo"}, {"id": "7c8d9e0f-1a2b-3c4d-5e6f-7a8b9c0d1e2f", "label": "Andar na ponta dos pés"}, {"id": "8d9e0f1a-2b3c-4d5e-6f7a-8b9c0d1e2f3a", "label": "Nenhum"}], "required": true},
   {"id": "sensorial", "type": "multiple-choice", "label": "Apresenta sensibilidade sensorial?", "options": [{"id": "9e0f1a2b-3c4d-5e6f-7a8b-9c0d1e2f3a4b", "label": "Sons altos"}, {"id": "0f1a2b3c-4d5e-6f7a-8b9c-0d1e2f3a4b5c", "label": "Texturas (roupas, alimentos)"}, {"id": "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c60", "label": "Luzes"}, {"id": "2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d70", "label": "Cheiros"}, {"id": "3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e80", "label": "Nenhuma"}], "required": true},

   {"id": "secao_observacoes", "type": "section-header", "label": "Informações Adicionais"},
   {"id": "diagnosticos_previos", "type": "long-text", "label": "Possui algum diagnóstico prévio? Qual?", "required": false},
   {"id": "medicacao", "type": "short-text", "label": "Faz uso de alguma medicação? Qual?", "required": false},
   {"id": "terapias_atuais", "type": "multiple-choice", "label": "Faz acompanhamento com outros profissionais?", "options": [{"id": "4d5e6f7a-8b9c-0d1e-2f3a-4b5c6d7e8f90", "label": "Fonoaudiologia"}, {"id": "5e6f7a8b-9c0d-1e2f-3a4b-5c6d7e8f9a00", "label": "Terapia Ocupacional"}, {"id": "6f7a8b9c-0d1e-2f3a-4b5c-6d7e8f9a0b10", "label": "Psicopedagogia"}, {"id": "7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c20", "label": "Neuropediatria"}, {"id": "8b9c0d1e-2f3a-4b5c-6d7e-8f9a0b1c2d30", "label": "Nenhum"}], "required": true},
   {"id": "observacoes_familia", "type": "long-text", "label": "Observações adicionais da família", "required": false}
 ]');

INSERT INTO ai_quotas (ai_tier, model, monthly_limit, overage_price_cents, enabled)
SELECT 'DOX_IA', 'claude-sonnet-4-6', 15, 900, true
WHERE NOT EXISTS (SELECT 1 FROM ai_quotas);

INSERT INTO event_tags (id, name, color) VALUES
('d1e2f3a4-b5c6-7d8e-9f0a-1b2c3d4e5f6a', 'Consulta',    '#007AFF'),
('e2f3a4b5-c6d7-8e9f-0a1b-2c3d4e5f6a7b', 'Retorno',     '#34C759'),
('f3a4b5c6-d7e8-9f0a-1b2c-3d4e5f6a7b8c', 'Avaliação',   '#FF9500'),
('a4b5c6d7-e8f9-0a1b-2c3d-4e5f6a7b8c9d', 'Devolutiva',  '#AF52DE'),
('b5c6d7e8-f9a0-1b2c-3d4e-5f6a7b8c9d0e', 'Supervisão',  '#5856D6');
