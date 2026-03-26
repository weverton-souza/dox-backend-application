ALTER TABLE report_templates ADD COLUMN IF NOT EXISTS is_locked BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE report_templates ADD COLUMN IF NOT EXISTS is_master BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE reports ADD COLUMN IF NOT EXISTS template_id UUID REFERENCES report_templates(id) ON DELETE SET NULL;
ALTER TABLE reports ADD COLUMN IF NOT EXISTS is_structure_locked BOOLEAN NOT NULL DEFAULT FALSE;

-- Seed: Template Mestre - Laudo Padrão Adulto
INSERT INTO report_templates (id, name, description, blocks, is_default, is_locked, is_master)
VALUES (
  '00000000-0000-0000-0000-000000000001',
  'Laudo Padrão Adulto',
  'Estrutura completa para avaliação de adultos',
  '[
    {"id":"tpl-adulto-id","type":"identification","order":0,"parentId":null,"data":{}},
    {"id":"tpl-adulto-s1","type":"section","order":1,"parentId":null,"data":{"title":"DESCRIÇÃO DA DEMANDA E OBJETIVOS DA AVALIAÇÃO","sectionType":"DESCRICAO_DEMANDA"}},
    {"id":"tpl-adulto-s1-t1","type":"text","order":2,"parentId":"tpl-adulto-s1","data":{}},
    {"id":"tpl-adulto-s2","type":"section","order":3,"parentId":null,"data":{"title":"PROCEDIMENTOS","sectionType":"PROCEDIMENTOS"}},
    {"id":"tpl-adulto-s2-t1","type":"text","order":4,"parentId":"tpl-adulto-s2","data":{}},
    {"id":"tpl-adulto-s3","type":"section","order":5,"parentId":null,"data":{"title":"ANAMNESE","sectionType":"ANAMNESE"}},
    {"id":"tpl-adulto-s3-t1","type":"text","order":6,"parentId":"tpl-adulto-s3","data":{}},
    {"id":"tpl-adulto-s4","type":"section","order":7,"parentId":null,"data":{"title":"RESULTADOS","sectionType":"RESULTADOS"}},
    {"id":"tpl-adulto-s4-st1","type":"score-table","order":8,"parentId":"tpl-adulto-s4","data":{"title":"ATENÇÃO"}},
    {"id":"tpl-adulto-s4-st2","type":"score-table","order":9,"parentId":"tpl-adulto-s4","data":{"title":"MEMÓRIA"}},
    {"id":"tpl-adulto-s4-st3","type":"score-table","order":10,"parentId":"tpl-adulto-s4","data":{"title":"FUNÇÕES EXECUTIVAS"}},
    {"id":"tpl-adulto-s4-ch1","type":"chart","order":11,"parentId":"tpl-adulto-s4","data":{"title":"DESEMPENHO"}},
    {"id":"tpl-adulto-s5","type":"section","order":12,"parentId":null,"data":{"title":"ANÁLISE E OBSERVAÇÕES","sectionType":"ANALISE_OBSERVACOES"}},
    {"id":"tpl-adulto-s5-t1","type":"text","order":13,"parentId":"tpl-adulto-s5","data":{}},
    {"id":"tpl-adulto-ib1","type":"info-box","order":14,"parentId":null,"data":{"label":"IMPRESSÃO DIAGNÓSTICA"}},
    {"id":"tpl-adulto-s6","type":"section","order":15,"parentId":null,"data":{"title":"SUGESTÕES E ENCAMINHAMENTOS","sectionType":"SUGESTOES_ENCAMINHAMENTOS"}},
    {"id":"tpl-adulto-s6-t1","type":"text","order":16,"parentId":"tpl-adulto-s6","data":{}},
    {"id":"tpl-adulto-s7","type":"section","order":17,"parentId":null,"data":{"title":"CONCLUSÃO","sectionType":"CONCLUSAO"}},
    {"id":"tpl-adulto-s7-t1","type":"text","order":18,"parentId":"tpl-adulto-s7","data":{}},
    {"id":"tpl-adulto-ref","type":"references","order":19,"parentId":null,"data":{}},
    {"id":"tpl-adulto-cp","type":"closing-page","order":20,"parentId":null,"data":{}}
  ]'::jsonb,
  true, true, true
) ON CONFLICT (id) DO NOTHING;

-- Seed: Template Mestre - Laudo Breve
INSERT INTO report_templates (id, name, description, blocks, is_default, is_locked, is_master)
VALUES (
  '00000000-0000-0000-0000-000000000002',
  'Laudo Breve',
  'Estrutura resumida para laudos mais curtos',
  '[
    {"id":"tpl-breve-id","type":"identification","order":0,"parentId":null,"data":{}},
    {"id":"tpl-breve-s1","type":"section","order":1,"parentId":null,"data":{"title":"DESCRIÇÃO DA DEMANDA","sectionType":"DESCRICAO_DEMANDA"}},
    {"id":"tpl-breve-s1-t1","type":"text","order":2,"parentId":"tpl-breve-s1","data":{}},
    {"id":"tpl-breve-s2","type":"section","order":3,"parentId":null,"data":{"title":"PROCEDIMENTOS","sectionType":"PROCEDIMENTOS"}},
    {"id":"tpl-breve-s2-t1","type":"text","order":4,"parentId":"tpl-breve-s2","data":{}},
    {"id":"tpl-breve-s3","type":"section","order":5,"parentId":null,"data":{"title":"RESULTADOS","sectionType":"RESULTADOS"}},
    {"id":"tpl-breve-s3-st1","type":"score-table","order":6,"parentId":"tpl-breve-s3","data":{"title":"RESULTADOS"}},
    {"id":"tpl-breve-ib1","type":"info-box","order":7,"parentId":null,"data":{"label":"IMPRESSÃO DIAGNÓSTICA"}},
    {"id":"tpl-breve-s4","type":"section","order":8,"parentId":null,"data":{"title":"CONCLUSÃO","sectionType":"CONCLUSAO"}},
    {"id":"tpl-breve-s4-t1","type":"text","order":9,"parentId":"tpl-breve-s4","data":{}},
    {"id":"tpl-breve-cp","type":"closing-page","order":10,"parentId":null,"data":{}}
  ]'::jsonb,
  true, true, true
) ON CONFLICT (id) DO NOTHING;
