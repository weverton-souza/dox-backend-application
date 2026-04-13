-- =============================================
-- Seeds de Report Templates Mestres
-- Depende de V1_11 (score_table_templates) e V1_12 (chart_templates)
-- =============================================

-- Template Mestre - Laudo Padrão Adulto
INSERT INTO report_templates (id, name, description, blocks, is_default, is_locked, is_master)
VALUES (
  '00000000-0000-0000-0000-000000000001',
  'Laudo Padrão Adulto',
  'Estrutura completa para avaliação de adultos',
  '[
    {"id":"tpl-adulto-id","type":"identification","order":0,"parentId":null,"data":{"professional":{"name":"","crp":"","specialization":""},"customer":{"name":"","birthDate":"","age":"","gender":"","education":"","laterality":"","cpf":"","rg":"","profession":"","naturalness":"","nationality":"","maritalStatus":"","phone":"","email":"","address":""},"date":"","location":""}},

    {"id":"tpl-adulto-s1","type":"section","order":1,"parentId":null,"data":{"title":"DESCRIÇÃO DA DEMANDA E OBJETIVOS DA AVALIAÇÃO","sectionType":"DESCRICAO_DEMANDA"}},
    {"id":"tpl-adulto-s1-t1","type":"text","order":2,"parentId":"tpl-adulto-s1","data":{"content":"","labeledItems":[],"useLabeledItems":false}},

    {"id":"tpl-adulto-s2","type":"section","order":3,"parentId":null,"data":{"title":"PROCEDIMENTOS","sectionType":"PROCEDIMENTOS"}},
    {"id":"tpl-adulto-s2-t1","type":"text","order":4,"parentId":"tpl-adulto-s2","data":{"content":"","labeledItems":[],"useLabeledItems":false}},

    {"id":"tpl-adulto-s3","type":"section","order":5,"parentId":null,"data":{"title":"ANAMNESE","sectionType":"ANAMNESE"}},
    {"id":"tpl-adulto-s3-t1","type":"text","order":6,"parentId":"tpl-adulto-s3","data":{"content":"","labeledItems":[],"useLabeledItems":false}},

    {"id":"tpl-adulto-s4","type":"section","order":7,"parentId":null,"data":{"title":"RESULTADOS","sectionType":"RESULTADOS"}},

    {"id":"tpl-adulto-s4-st1","type":"score-table","order":8,"parentId":"tpl-adulto-s4","data":{
      "title":"Tabela: Atenção e Velocidade de Processamento",
      "templateId":"a1000003-0000-4000-8000-000000000003",
      "columns":[
        {"id":"atv-col-instrumento","label":"Instrumento","alignment":"left"},
        {"id":"atv-col-max","label":"Máximo"},
        {"id":"atv-col-ref","label":"Referência"},
        {"id":"atv-col-obtido","label":"Obtido"},
        {"id":"atv-col-interp","label":"Classificação","alignment":"left"}
      ],
      "rows":[
        {"id":"atv-row-0","values":{"atv-col-instrumento":"Dígitos Ordem Direta (WAIS-III)","atv-col-max":"19","atv-col-ref":"8 a 12","atv-col-interp":"=CLASSIFICAR(PORCENTAGEM(D1;B1);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"atv-row-1","values":{"atv-col-instrumento":"Procurar Símbolos (WAIS-III)","atv-col-max":"19","atv-col-ref":"8 a 12","atv-col-interp":"=CLASSIFICAR(PORCENTAGEM(D2;B2);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"atv-row-2","values":{"atv-col-instrumento":"Código (WAIS-III)","atv-col-max":"19","atv-col-ref":"8 a 12","atv-col-interp":"=CLASSIFICAR(PORCENTAGEM(D3;B3);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"atv-row-3","values":{"atv-col-instrumento":"Leitura – Tempo (FDT)","atv-col-max":"95","atv-col-ref":"25 a 75","atv-col-interp":"=CLASSIFICAR(PORCENTAGEM(D4;B4);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"atv-row-4","values":{"atv-col-instrumento":"Leitura – Erros (FDT)","atv-col-max":"95","atv-col-ref":"25 a 75","atv-col-interp":"=CLASSIFICAR(PORCENTAGEM(D5;B5);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"atv-row-5","values":{"atv-col-instrumento":"Contagem – Tempo (FDT)","atv-col-max":"95","atv-col-ref":"25 a 75","atv-col-interp":"=CLASSIFICAR(PORCENTAGEM(D6;B6);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"atv-row-6","values":{"atv-col-instrumento":"Contagem – Erros (FDT)","atv-col-max":"95","atv-col-ref":"25 a 75","atv-col-interp":"=CLASSIFICAR(PORCENTAGEM(D7;B7);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}}
      ],
      "footnote":""
    }},

    {"id":"tpl-adulto-s4-st2","type":"score-table","order":9,"parentId":"tpl-adulto-s4","data":{
      "title":"Tabela: Memória",
      "templateId":"a1000005-0000-4000-8000-000000000005",
      "columns":[
        {"id":"mem-col-instrumento","label":"Instrumento","alignment":"left"},
        {"id":"mem-col-max","label":"Máximo"},
        {"id":"mem-col-ref","label":"Referência"},
        {"id":"mem-col-obtido","label":"Obtido"},
        {"id":"mem-col-interp","label":"Classificação","alignment":"left"}
      ],
      "rows":[
        {"id":"mem-row-0","values":{"mem-col-instrumento":"Dígitos Ordem Inversa (WAIS-III)","mem-col-max":"19","mem-col-ref":"8 a 12","mem-col-interp":"=CLASSIFICAR(PORCENTAGEM(D1;B1);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"mem-row-1","values":{"mem-col-instrumento":"Aritmética (WAIS-III)","mem-col-max":"19","mem-col-ref":"8 a 12","mem-col-interp":"=CLASSIFICAR(PORCENTAGEM(D2;B2);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"mem-row-2","values":{"mem-col-instrumento":"Vocabulário (WAIS-III)","mem-col-max":"19","mem-col-ref":"8 a 12","mem-col-interp":"=CLASSIFICAR(PORCENTAGEM(D3;B3);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"mem-row-3","values":{"mem-col-instrumento":"Informação (WAIS-III)","mem-col-max":"19","mem-col-ref":"8 a 12","mem-col-interp":"=CLASSIFICAR(PORCENTAGEM(D4;B4);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"mem-row-4","values":{"mem-col-instrumento":"Sequência de Números e Letras (WAIS-III)","mem-col-max":"19","mem-col-ref":"8 a 12","mem-col-interp":"=CLASSIFICAR(PORCENTAGEM(D5;B5);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"mem-row-5","values":{"mem-col-instrumento":"Figuras Complexas de Rey (Memória)","mem-col-max":"100","mem-col-ref":"50","mem-col-interp":"=CLASSIFICAR(PORCENTAGEM(D6;B6);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Prejuízo leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"mem-row-6","values":{"mem-col-instrumento":"Figuras Complexas de Rey (Memória – Tempo)","mem-col-max":"100","mem-col-ref":"25 a 75","mem-col-interp":"=CLASSIFICAR(PORCENTAGEM(D7;B7);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Prejuízo leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}}
      ],
      "footnote":""
    }},

    {"id":"tpl-adulto-s4-st3","type":"score-table","order":10,"parentId":"tpl-adulto-s4","data":{
      "title":"Tabela: Funções Executivas",
      "templateId":"a1000006-0000-4000-8000-000000000006",
      "columns":[
        {"id":"fe-col-instrumento","label":"Instrumento","alignment":"left"},
        {"id":"fe-col-max","label":"Máximo"},
        {"id":"fe-col-ref","label":"Referência"},
        {"id":"fe-col-obtido","label":"Obtido"},
        {"id":"fe-col-interp","label":"Classificação","alignment":"left"}
      ],
      "rows":[
        {"id":"fe-row-0","values":{"fe-col-instrumento":"Dígitos Ordem Inversa (WAIS-III)","fe-col-max":"14","fe-col-ref":"8 a 12","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D1;B1);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"fe-row-1","values":{"fe-col-instrumento":"Sequência de Números e Letras (WAIS-III)","fe-col-max":"19","fe-col-ref":"8 a 12","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D2;B2);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"fe-row-2","values":{"fe-col-instrumento":"Figuras Complexas de Rey (Cópia)","fe-col-max":"100","fe-col-ref":"50","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D3;B3);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Prejuízo leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"fe-row-3","values":{"fe-col-instrumento":"Figuras Complexas de Rey (Cópia – Tempo)","fe-col-max":"100","fe-col-ref":"25 a 75","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D4;B4);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Prejuízo leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"fe-row-4","values":{"fe-col-instrumento":"Torre de Londres – ToL-BR","fe-col-max":"100","fe-col-ref":"50","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D5;B5);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Prejuízo leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"fe-row-5","values":{"fe-col-instrumento":"Escolha – Tempo (FDT)","fe-col-max":"95","fe-col-ref":"25 a 75","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D6;B6);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"fe-row-6","values":{"fe-col-instrumento":"Escolha – Erros (FDT)","fe-col-max":"95","fe-col-ref":"25 a 75","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D7;B7);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"fe-row-7","values":{"fe-col-instrumento":"Alternância – Tempo (FDT)","fe-col-max":"95","fe-col-ref":"25 a 75","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D8;B8);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"fe-row-8","values":{"fe-col-instrumento":"Alternância – Erros (FDT)","fe-col-max":"95","fe-col-ref":"25 a 75","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D9;B9);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"fe-row-9","values":{"fe-col-instrumento":"Inibição (FDT)","fe-col-max":"95","fe-col-ref":"25 a 75","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D10;B10);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
        {"id":"fe-row-10","values":{"fe-col-instrumento":"Flexibilidade (FDT)","fe-col-max":"95","fe-col-ref":"25 a 75","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D11;B11);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}}
      ],
      "footnote":""
    }},

    {"id":"tpl-adulto-s4-ch1","type":"chart","order":11,"parentId":"tpl-adulto-s4","data":{
      "title":"Gráfico: WAIS-III — Subtestes",
      "chartType":"bar",
      "displayMode":"separated",
      "series":[{"id":"wais-verb","label":"Subtestes Verbais","color":"#0984E3"},{"id":"wais-exec","label":"Subtestes de Execução","color":"#00B894"}],
      "categories":[
        {"id":"c0","label":"VC","values":{"wais-verb":0}},
        {"id":"c1","label":"SM","values":{"wais-verb":0}},
        {"id":"c2","label":"AR","values":{"wais-verb":0}},
        {"id":"c3","label":"DG","values":{"wais-verb":0}},
        {"id":"c4","label":"IN","values":{"wais-verb":0}},
        {"id":"c5","label":"CO","values":{"wais-verb":0}},
        {"id":"c6","label":"SNL","values":{"wais-verb":0}},
        {"id":"c7","label":"CF","values":{"wais-exec":0}},
        {"id":"c8","label":"CD","values":{"wais-exec":0}},
        {"id":"c9","label":"CB","values":{"wais-exec":0}},
        {"id":"c10","label":"RM","values":{"wais-exec":0}},
        {"id":"c11","label":"AF","values":{"wais-exec":0}},
        {"id":"c12","label":"PS","values":{"wais-exec":0}}
      ],
      "referenceLines":[],
      "referenceRegions":[{"id":"wr1","label":"Média","yMin":8,"yMax":12,"color":"#00B89422","borderColor":"#00B89422"}],
      "yAxisLabel":"Escore Ponderado",
      "showValues":true,
      "showLegend":true,
      "showRegionLegend":true,
      "description":""
    }},

    {"id":"tpl-adulto-s5","type":"section","order":12,"parentId":null,"data":{"title":"ANÁLISE E OBSERVAÇÕES","sectionType":"ANALISE_OBSERVACOES"}},
    {"id":"tpl-adulto-s5-t1","type":"text","order":13,"parentId":"tpl-adulto-s5","data":{"content":"","labeledItems":[],"useLabeledItems":false}},
    {"id":"tpl-adulto-ib1","type":"info-box","order":14,"parentId":"tpl-adulto-s5","data":{"label":"IMPRESSÃO DIAGNÓSTICA","content":""}},

    {"id":"tpl-adulto-s6","type":"section","order":15,"parentId":null,"data":{"title":"SUGESTÕES E ENCAMINHAMENTOS","sectionType":"SUGESTOES_ENCAMINHAMENTOS"}},
    {"id":"tpl-adulto-s6-t1","type":"text","order":16,"parentId":"tpl-adulto-s6","data":{"content":"","labeledItems":[],"useLabeledItems":false}},

    {"id":"tpl-adulto-s7","type":"section","order":17,"parentId":null,"data":{"title":"CONCLUSÃO","sectionType":"CONCLUSAO"}},
    {"id":"tpl-adulto-s7-t1","type":"text","order":18,"parentId":"tpl-adulto-s7","data":{"content":"","labeledItems":[],"useLabeledItems":false}},

    {"id":"tpl-adulto-s8","type":"section","order":19,"parentId":null,"data":{"title":"REFERÊNCIAS BIBLIOGRÁFICAS","sectionType":"REFERENCIAS"}},
    {"id":"tpl-adulto-ref","type":"references","order":20,"parentId":"tpl-adulto-s8","data":{"title":"REFERÊNCIAS BIBLIOGRÁFICAS","references":[""]}},

    {"id":"tpl-adulto-cp","type":"closing-page","order":21,"parentId":null,"data":{"title":"TERMO DE ENTREGA E CIÊNCIA","bodyText":"Declaro que recebi o presente documento, contendo os resultados da avaliação realizada, e que fui orientado(a) sobre o conteúdo do mesmo. Estou ciente de que este relatório é de caráter confidencial e que as informações aqui contidas são de uso exclusivo para os fins a que se destina.","showPatientSignature":true,"showMotherSignature":false,"showFatherSignature":false,"showGuardianSignature":false,"footerNote":""}}
  ]'::jsonb,
  true, true, true
) ON CONFLICT (id) DO NOTHING;

-- Template Mestre - Laudo Breve
INSERT INTO report_templates (id, name, description, blocks, is_default, is_locked, is_master)
VALUES (
  '00000000-0000-0000-0000-000000000002',
  'Laudo Breve',
  'Estrutura resumida para laudos mais curtos',
  '[
    {"id":"tpl-breve-id","type":"identification","order":0,"parentId":null,"data":{"professional":{"name":"","crp":"","specialization":""},"customer":{"name":"","birthDate":"","age":"","gender":"","education":"","laterality":"","cpf":"","rg":"","profession":"","naturalness":"","nationality":"","maritalStatus":"","phone":"","email":"","address":""},"date":"","location":""}},

    {"id":"tpl-breve-s1","type":"section","order":1,"parentId":null,"data":{"title":"DESCRIÇÃO DA DEMANDA","sectionType":"DESCRICAO_DEMANDA"}},
    {"id":"tpl-breve-s1-t1","type":"text","order":2,"parentId":"tpl-breve-s1","data":{"content":"","labeledItems":[],"useLabeledItems":false}},

    {"id":"tpl-breve-s2","type":"section","order":3,"parentId":null,"data":{"title":"PROCEDIMENTOS","sectionType":"PROCEDIMENTOS"}},
    {"id":"tpl-breve-s2-t1","type":"text","order":4,"parentId":"tpl-breve-s2","data":{"content":"","labeledItems":[],"useLabeledItems":false}},

    {"id":"tpl-breve-s3","type":"section","order":5,"parentId":null,"data":{"title":"RESULTADOS","sectionType":"RESULTADOS"}},
    {"id":"tpl-breve-s3-st1","type":"score-table","order":6,"parentId":"tpl-breve-s3","data":{
      "title":"RESULTADOS",
      "columns":[
        {"id":"col-instrumento","label":"Instrumento/Subteste"},
        {"id":"col-valor-obtido","label":"Valor Obtido"},
        {"id":"col-percentil","label":"Percentil"},
        {"id":"col-classificacao","label":"Classificação"}
      ],
      "rows":[],
      "footnote":""
    }},

    {"id":"tpl-breve-s3b","type":"section","order":7,"parentId":null,"data":{"title":"IMPRESSÃO DIAGNÓSTICA","sectionType":"IMPRESSAO_DIAGNOSTICA"}},
    {"id":"tpl-breve-ib1","type":"info-box","order":8,"parentId":"tpl-breve-s3b","data":{"label":"IMPRESSÃO DIAGNÓSTICA","content":""}},

    {"id":"tpl-breve-s4","type":"section","order":9,"parentId":null,"data":{"title":"CONCLUSÃO","sectionType":"CONCLUSAO"}},
    {"id":"tpl-breve-s4-t1","type":"text","order":10,"parentId":"tpl-breve-s4","data":{"content":"","labeledItems":[],"useLabeledItems":false}},

    {"id":"tpl-breve-cp","type":"closing-page","order":11,"parentId":null,"data":{"title":"TERMO DE ENTREGA E CIÊNCIA","bodyText":"Declaro que recebi o presente documento, contendo os resultados da avaliação realizada, e que fui orientado(a) sobre o conteúdo do mesmo. Estou ciente de que este relatório é de caráter confidencial e que as informações aqui contidas são de uso exclusivo para os fins a que se destina.","showPatientSignature":true,"showMotherSignature":false,"showFatherSignature":false,"showGuardianSignature":false,"footerNote":""}}
  ]'::jsonb,
  true, true, true
) ON CONFLICT (id) DO NOTHING;
