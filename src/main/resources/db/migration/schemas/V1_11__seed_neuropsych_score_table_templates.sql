-- =============================================
-- Templates de tabelas de escores para avaliação neuropsicológica
-- Baseado em laudo real de avaliação neuropsicológica adulto
-- =============================================

-- 1. WAIS-III Índices Compostos
-- Colunas: A=Índices, B=Soma PP, C=Composto, D=Classificação, E=Percentil, F=IC 95%
INSERT INTO score_table_templates (id, name, description, instrument_name, category, columns, rows, footnote, is_default) VALUES
('a1000001-0000-4000-8000-000000000001', 'Tabela: WAIS-III Índices', 'Resultados e conversão da soma dos pontos ponderados em pontos compostos — QIs e Índices Fatoriais', 'WAIS-III', 'Inteligência',
'[
  {"id":"wais-idx-col-indice","label":"Índices","formula":null,"alignment":"left"},
  {"id":"wais-idx-col-soma","label":"Soma PP","formula":null},
  {"id":"wais-idx-col-composto","label":"Composto","formula":null},
  {"id":"wais-idx-col-classificacao","label":"Classificação","formula":null,"alignment":"left","alignment":"left"},
  {"id":"wais-idx-col-percentil","label":"Percentil","formula":null},
  {"id":"wais-idx-col-ic","label":"IC 95%","formula":null}
]'::JSONB,
'[
  {"id":"wais-idx-row-0","defaultValues":{"wais-idx-col-indice":"Compreensão Verbal (ICV)","wais-idx-col-classificacao":"=CLASSIFICAR(C1;>=130;\"● Muito Superior\"@#81C784;>=120;\"● Superior\"@#A5D6A7;>=110;\"● Médio Superior\"@#C5E1A5;>=90;\"● Média\"@#64B5F6;>=80;\"● Médio Inferior\"@#FFB74D;>=70;\"● Limítrofe\"@#FF8A65;\"● Extremamente Baixo\"@#EF9A9A)"}},
  {"id":"wais-idx-row-1","defaultValues":{"wais-idx-col-indice":"Organização Perceptual (IOP)","wais-idx-col-classificacao":"=CLASSIFICAR(C2;>=130;\"● Muito Superior\"@#81C784;>=120;\"● Superior\"@#A5D6A7;>=110;\"● Médio Superior\"@#C5E1A5;>=90;\"● Média\"@#64B5F6;>=80;\"● Médio Inferior\"@#FFB74D;>=70;\"● Limítrofe\"@#FF8A65;\"● Extremamente Baixo\"@#EF9A9A)"}},
  {"id":"wais-idx-row-2","defaultValues":{"wais-idx-col-indice":"Memória Operacional (IMO)","wais-idx-col-classificacao":"=CLASSIFICAR(C3;>=130;\"● Muito Superior\"@#81C784;>=120;\"● Superior\"@#A5D6A7;>=110;\"● Médio Superior\"@#C5E1A5;>=90;\"● Média\"@#64B5F6;>=80;\"● Médio Inferior\"@#FFB74D;>=70;\"● Limítrofe\"@#FF8A65;\"● Extremamente Baixo\"@#EF9A9A)"}},
  {"id":"wais-idx-row-3","defaultValues":{"wais-idx-col-indice":"Velocidade de Processamento (IVP)","wais-idx-col-classificacao":"=CLASSIFICAR(C4;>=130;\"● Muito Superior\"@#81C784;>=120;\"● Superior\"@#A5D6A7;>=110;\"● Médio Superior\"@#C5E1A5;>=90;\"● Média\"@#64B5F6;>=80;\"● Médio Inferior\"@#FFB74D;>=70;\"● Limítrofe\"@#FF8A65;\"● Extremamente Baixo\"@#EF9A9A)"}},
  {"id":"wais-idx-row-4","defaultValues":{"wais-idx-col-indice":"QI Verbal (QIV)","wais-idx-col-classificacao":"=CLASSIFICAR(C5;>=130;\"● Muito Superior\"@#81C784;>=120;\"● Superior\"@#A5D6A7;>=110;\"● Médio Superior\"@#C5E1A5;>=90;\"● Média\"@#64B5F6;>=80;\"● Médio Inferior\"@#FFB74D;>=70;\"● Limítrofe\"@#FF8A65;\"● Extremamente Baixo\"@#EF9A9A)"}},
  {"id":"wais-idx-row-5","defaultValues":{"wais-idx-col-indice":"QI de Execução (QIE)","wais-idx-col-classificacao":"=CLASSIFICAR(C6;>=130;\"● Muito Superior\"@#81C784;>=120;\"● Superior\"@#A5D6A7;>=110;\"● Médio Superior\"@#C5E1A5;>=90;\"● Média\"@#64B5F6;>=80;\"● Médio Inferior\"@#FFB74D;>=70;\"● Limítrofe\"@#FF8A65;\"● Extremamente Baixo\"@#EF9A9A)"}},
  {"id":"wais-idx-row-6","defaultValues":{"wais-idx-col-indice":"QI Total (QIT)","wais-idx-col-classificacao":"=CLASSIFICAR(C7;>=130;\"● Muito Superior\"@#81C784;>=120;\"● Superior\"@#A5D6A7;>=110;\"● Médio Superior\"@#C5E1A5;>=90;\"● Média\"@#64B5F6;>=80;\"● Médio Inferior\"@#FFB74D;>=70;\"● Limítrofe\"@#FF8A65;\"● Extremamente Baixo\"@#EF9A9A)"}}
]'::JSONB,
'[{"type":"p","children":[{"text":"Nota: ","bold":true,"italic":true},{"text":"PP","bold":true},{"text":" = Pontos Ponderados · "},{"text":"IC 95%","bold":true},{"text":" = Intervalo de Confiança 95% · "},{"text":"Percentil","bold":true},{"text":" = posição relativa comparada à população da mesma faixa etária (ex: 99,6 = supera 99,6% das pessoas de mesma idade)"}]}]'::JSONB,
true);

-- 2. Percepção e Noções Básicas
-- Colunas: A=Instrumentos, B=Máximo, C=Referência, D=Obtido, E=Interpretação
INSERT INTO score_table_templates (id, name, description, instrument_name, category, columns, rows, is_default) VALUES
('a1000002-0000-4000-8000-000000000002', 'Tabela: Percepção e Noções Básicas', 'Avaliação de habilidades visuoespaciais, visuoconstrutivas e percepção visual', 'Diversos', 'Percepção',
'[
  {"id":"perc-col-instrumento","label":"Instrumento","formula":null,"alignment":"left"},
  {"id":"perc-col-max","label":"Máximo","formula":null},
  {"id":"perc-col-ref","label":"Referência","formula":null},
  {"id":"perc-col-obtido","label":"Obtido","formula":null},
  {"id":"perc-col-interp","label":"Classificação","formula":null,"alignment":"left"}
]'::JSONB,
'[
  {"id":"perc-row-0","defaultValues":{"perc-col-instrumento":"Cubos (WAIS-III)","perc-col-max":"19","perc-col-ref":"8 a 12","perc-col-interp":"=CLASSIFICAR(PORCENTAGEM(D1;B1);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Prejuízo leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"perc-row-1","defaultValues":{"perc-col-instrumento":"Completar Figuras (WAIS-III)","perc-col-max":"19","perc-col-ref":"8 a 12","perc-col-interp":"=CLASSIFICAR(PORCENTAGEM(D2;B2);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Prejuízo leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"perc-row-2","defaultValues":{"perc-col-instrumento":"Arranjo de Figuras (WAIS-III)","perc-col-max":"19","perc-col-ref":"8 a 12","perc-col-interp":"=CLASSIFICAR(PORCENTAGEM(D3;B3);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Prejuízo leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"perc-row-3","defaultValues":{"perc-col-instrumento":"Raciocínio Matricial (WAIS-III)","perc-col-max":"19","perc-col-ref":"8 a 12","perc-col-interp":"=CLASSIFICAR(PORCENTAGEM(D4;B4);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Prejuízo leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"perc-row-4","defaultValues":{"perc-col-instrumento":"Figuras Complexas de Rey (Cópia)","perc-col-max":"100","perc-col-ref":"50","perc-col-interp":"=CLASSIFICAR(PORCENTAGEM(D5;B5);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Prejuízo leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"perc-row-5","defaultValues":{"perc-col-instrumento":"Figuras Complexas de Rey (Cópia – Tempo)","perc-col-max":"100","perc-col-ref":"25 a 75","perc-col-interp":"=CLASSIFICAR(PORCENTAGEM(D6;B6);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Prejuízo leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}}
]'::JSONB,
true);

-- 3. Atenção e Velocidade de Processamento
-- Colunas: A=Instrumentos, B=Máximo, C=Referência, D=Obtido, E=Interpretação
INSERT INTO score_table_templates (id, name, description, instrument_name, category, columns, rows, is_default) VALUES
('a1000003-0000-4000-8000-000000000003', 'Tabela: Atenção e Velocidade de Processamento', 'Avaliação de atenção seletiva, alternada, dividida e velocidade de processamento', 'WAIS-III / FDT', 'Atenção',
'[
  {"id":"atv-col-instrumento","label":"Instrumento","formula":null,"alignment":"left"},
  {"id":"atv-col-max","label":"Máximo","formula":null},
  {"id":"atv-col-ref","label":"Referência","formula":null},
  {"id":"atv-col-obtido","label":"Obtido","formula":null},
  {"id":"atv-col-interp","label":"Classificação","formula":null,"alignment":"left"}
]'::JSONB,
'[
  {"id":"atv-row-0","defaultValues":{"atv-col-instrumento":"Dígitos Ordem Direta (WAIS-III)","atv-col-max":"19","atv-col-ref":"8 a 12","atv-col-interp":"=CLASSIFICAR(PORCENTAGEM(D1;B1);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"atv-row-1","defaultValues":{"atv-col-instrumento":"Procurar Símbolos (WAIS-III)","atv-col-max":"19","atv-col-ref":"8 a 12","atv-col-interp":"=CLASSIFICAR(PORCENTAGEM(D2;B2);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"atv-row-2","defaultValues":{"atv-col-instrumento":"Código (WAIS-III)","atv-col-max":"19","atv-col-ref":"8 a 12","atv-col-interp":"=CLASSIFICAR(PORCENTAGEM(D3;B3);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"atv-row-3","defaultValues":{"atv-col-instrumento":"Leitura – Tempo (FDT)","atv-col-max":"95","atv-col-ref":"25 a 75","atv-col-interp":"=CLASSIFICAR(PORCENTAGEM(D4;B4);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"atv-row-4","defaultValues":{"atv-col-instrumento":"Leitura – Erros (FDT)","atv-col-max":"95","atv-col-ref":"25 a 75","atv-col-interp":"=CLASSIFICAR(PORCENTAGEM(D5;B5);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"atv-row-5","defaultValues":{"atv-col-instrumento":"Contagem – Tempo (FDT)","atv-col-max":"95","atv-col-ref":"25 a 75","atv-col-interp":"=CLASSIFICAR(PORCENTAGEM(D6;B6);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"atv-row-6","defaultValues":{"atv-col-instrumento":"Contagem – Erros (FDT)","atv-col-max":"95","atv-col-ref":"25 a 75","atv-col-interp":"=CLASSIFICAR(PORCENTAGEM(D7;B7);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}}
]'::JSONB,
true);

-- 4. BPA-2 — Bateria Psicológica para Avaliação da Atenção
-- Colunas: A=Atenção, B=Pontos, C=Percentil, D=Classificação
INSERT INTO score_table_templates (id, name, description, instrument_name, category, columns, rows, is_default) VALUES
('a1000004-0000-4000-8000-000000000004', 'Tabela: BPA-2', 'Bateria Psicológica para Avaliação da Atenção — atenção concentrada, dividida, alternada e geral', 'BPA-2', 'Atenção',
'[
  {"id":"bpa-col-atencao","label":"Atenção","formula":null,"alignment":"left"},
  {"id":"bpa-col-pontos","label":"Pontos","formula":null},
  {"id":"bpa-col-percentil","label":"Percentil","formula":null},
  {"id":"bpa-col-classificacao","label":"Classificação","formula":null,"alignment":"left"}
]'::JSONB,
'[
  {"id":"bpa-row-0","defaultValues":{"bpa-col-atencao":"Atenção Concentrada - AC","bpa-col-classificacao":"=CLASSIFICAR(C1;>=96;\"● Muito Superior\"@#81C784;>=85;\"● Superior\"@#A5D6A7;>=70;\"● Médio Superior\"@#C5E1A5;>=30;\"● Média\"@#64B5F6;>=15;\"● Médio Inferior\"@#FFB74D;>=5;\"● Inferior\"@#FF8A65;\"●Muito Inferior\"@#EF9A9A)"}},
  {"id":"bpa-row-1","defaultValues":{"bpa-col-atencao":"Atenção Dividida - AD","bpa-col-classificacao":"=CLASSIFICAR(C2;>=96;\"● Muito Superior\"@#81C784;>=85;\"● Superior\"@#A5D6A7;>=70;\"● Médio Superior\"@#C5E1A5;>=30;\"● Média\"@#64B5F6;>=15;\"● Médio Inferior\"@#FFB74D;>=5;\"● Inferior\"@#FF8A65;\"●Muito Inferior\"@#EF9A9A)"}},
  {"id":"bpa-row-2","defaultValues":{"bpa-col-atencao":"Atenção Alternada - AA","bpa-col-classificacao":"=CLASSIFICAR(C3;>=96;\"● Muito Superior\"@#81C784;>=85;\"● Superior\"@#A5D6A7;>=70;\"● Médio Superior\"@#C5E1A5;>=30;\"● Média\"@#64B5F6;>=15;\"● Médio Inferior\"@#FFB74D;>=5;\"● Inferior\"@#FF8A65;\"●Muito Inferior\"@#EF9A9A)"}},
  {"id":"bpa-row-3","defaultValues":{"bpa-col-atencao":"Atenção Geral - AG","bpa-col-classificacao":"=CLASSIFICAR(C4;>=96;\"● Muito Superior\"@#81C784;>=85;\"● Superior\"@#A5D6A7;>=70;\"● Médio Superior\"@#C5E1A5;>=30;\"● Média\"@#64B5F6;>=15;\"● Médio Inferior\"@#FFB74D;>=5;\"● Inferior\"@#FF8A65;\"●Muito Inferior\"@#EF9A9A)"}}
]'::JSONB,
true);

-- 5. Memória
-- Colunas: A=Instrumentos, B=Máximo, C=Referência, D=Obtido, E=Interpretação
INSERT INTO score_table_templates (id, name, description, instrument_name, category, columns, rows, is_default) VALUES
('a1000005-0000-4000-8000-000000000005', 'Tabela: Memória', 'Avaliação de memória operacional, de curto prazo, semântica e visual', 'WAIS-III / Figuras de Rey', 'Memória',
'[
  {"id":"mem-col-instrumento","label":"Instrumento","formula":null,"alignment":"left"},
  {"id":"mem-col-max","label":"Máximo","formula":null},
  {"id":"mem-col-ref","label":"Referência","formula":null},
  {"id":"mem-col-obtido","label":"Obtido","formula":null},
  {"id":"mem-col-interp","label":"Classificação","formula":null,"alignment":"left"}
]'::JSONB,
'[
  {"id":"mem-row-0","defaultValues":{"mem-col-instrumento":"Dígitos Ordem Inversa (WAIS-III)","mem-col-max":"19","mem-col-ref":"8 a 12","mem-col-interp":"=CLASSIFICAR(PORCENTAGEM(D1;B1);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"mem-row-1","defaultValues":{"mem-col-instrumento":"Aritmética (WAIS-III)","mem-col-max":"19","mem-col-ref":"8 a 12","mem-col-interp":"=CLASSIFICAR(PORCENTAGEM(D2;B2);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"mem-row-2","defaultValues":{"mem-col-instrumento":"Vocabulário (WAIS-III)","mem-col-max":"19","mem-col-ref":"8 a 12","mem-col-interp":"=CLASSIFICAR(PORCENTAGEM(D3;B3);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"mem-row-3","defaultValues":{"mem-col-instrumento":"Informação (WAIS-III)","mem-col-max":"19","mem-col-ref":"8 a 12","mem-col-interp":"=CLASSIFICAR(PORCENTAGEM(D4;B4);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"mem-row-4","defaultValues":{"mem-col-instrumento":"Sequência de Números e Letras (WAIS-III)","mem-col-max":"19","mem-col-ref":"8 a 12","mem-col-interp":"=CLASSIFICAR(PORCENTAGEM(D5;B5);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"mem-row-5","defaultValues":{"mem-col-instrumento":"Figuras Complexas de Rey (Memória)","mem-col-max":"100","mem-col-ref":"50","mem-col-interp":"=CLASSIFICAR(PORCENTAGEM(D6;B6);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Prejuízo leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"mem-row-6","defaultValues":{"mem-col-instrumento":"Figuras Complexas de Rey (Memória – Tempo)","mem-col-max":"100","mem-col-ref":"25 a 75","mem-col-interp":"=CLASSIFICAR(PORCENTAGEM(D7;B7);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Prejuízo leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}}
]'::JSONB,
true);

-- 6. Funções Executivas
-- Colunas: A=Instrumentos, B=Máximo, C=Referência, D=Obtido, E=Interpretação
INSERT INTO score_table_templates (id, name, description, instrument_name, category, columns, rows, is_default) VALUES
('a1000006-0000-4000-8000-000000000006', 'Tabela: Funções Executivas', 'Avaliação de memória operacional, controle inibitório, flexibilidade cognitiva e planejamento', 'WAIS-III / FDT / Rey / ToL-BR', 'Funções Executivas',
'[
  {"id":"fe-col-instrumento","label":"Instrumento","formula":null,"alignment":"left"},
  {"id":"fe-col-max","label":"Máximo","formula":null},
  {"id":"fe-col-ref","label":"Referência","formula":null},
  {"id":"fe-col-obtido","label":"Obtido","formula":null},
  {"id":"fe-col-interp","label":"Classificação","formula":null,"alignment":"left"}
]'::JSONB,
'[
  {"id":"fe-row-0","defaultValues":{"fe-col-instrumento":"Dígitos Ordem Inversa (WAIS-III)","fe-col-max":"14","fe-col-ref":"8 a 12","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D1;B1);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"fe-row-1","defaultValues":{"fe-col-instrumento":"Sequência de Números e Letras (WAIS-III)","fe-col-max":"19","fe-col-ref":"8 a 12","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D2;B2);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"fe-row-2","defaultValues":{"fe-col-instrumento":"Figuras Complexas de Rey (Cópia)","fe-col-max":"100","fe-col-ref":"50","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D3;B3);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Prejuízo leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"fe-row-3","defaultValues":{"fe-col-instrumento":"Figuras Complexas de Rey (Cópia – Tempo)","fe-col-max":"100","fe-col-ref":"25 a 75","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D4;B4);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Prejuízo leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"fe-row-4","defaultValues":{"fe-col-instrumento":"Torre de Londres – ToL-BR","fe-col-max":"100","fe-col-ref":"50","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D5;B5);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Prejuízo leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"fe-row-5","defaultValues":{"fe-col-instrumento":"Escolha – Tempo (FDT)","fe-col-max":"95","fe-col-ref":"25 a 75","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D6;B6);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"fe-row-6","defaultValues":{"fe-col-instrumento":"Escolha – Erros (FDT)","fe-col-max":"95","fe-col-ref":"25 a 75","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D7;B7);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"fe-row-7","defaultValues":{"fe-col-instrumento":"Alternância – Tempo (FDT)","fe-col-max":"95","fe-col-ref":"25 a 75","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D8;B8);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"fe-row-8","defaultValues":{"fe-col-instrumento":"Alternância – Erros (FDT)","fe-col-max":"95","fe-col-ref":"25 a 75","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D9;B9);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"fe-row-9","defaultValues":{"fe-col-instrumento":"Inibição (FDT)","fe-col-max":"95","fe-col-ref":"25 a 75","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D10;B10);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}},
  {"id":"fe-row-10","defaultValues":{"fe-col-instrumento":"Flexibilidade (FDT)","fe-col-max":"95","fe-col-ref":"25 a 75","fe-col-interp":"=CLASSIFICAR(PORCENTAGEM(D11;B11);>=80;\"● Superior/ Facilidade\"@#A5D6A7;>=65;\"●Média Superior\"@#C5E1A5;>=40;\"● Média/Adequado\"@#64B5F6;>=20;\"● Inferior/ Dificuldade leve\"@#FFB74D;\"● Inferior/ Prejuízo grave\"@#EF9A9A)"}}
]'::JSONB,
true);

-- 7. ETDAH-AD — Escala de TDAH para Adultos
-- Colunas: A=Fator, B=Referência, C=Obtido, D=Classificação
INSERT INTO score_table_templates (id, name, description, instrument_name, category, columns, rows, footnote, is_default) VALUES
('a1000007-0000-4000-8000-000000000007', 'Tabela: ETDAH-AD', 'Escala de Transtorno do Déficit de Atenção e Hiperatividade — Versão Adolescentes e Adultos', 'ETDAH-AD', 'Comportamento',
'[
  {"id":"etdah-col-fator","label":"Fator","formula":null,"alignment":"left"},
  {"id":"etdah-col-ref","label":"Referência","formula":null},
  {"id":"etdah-col-obtido","label":"Obtido","formula":null},
  {"id":"etdah-col-classificacao","label":"Classificação","formula":null,"alignment":"left"}
]'::JSONB,
'[
  {"id":"etdah-row-0","defaultValues":{"etdah-col-fator":"Desatenção","etdah-col-ref":"25 a 80","etdah-col-classificacao":"=CLASSIFICAR(C1;>=81;\"● Superior/ Dificuldade\"@#EF9A9A;>=25;\"● Média Superior/ Adequado\"@#64B5F6;\"● Média/ Adequado\"@#A5D6A7)"}},
  {"id":"etdah-row-1","defaultValues":{"etdah-col-fator":"Impulsividade","etdah-col-ref":"25 a 80","etdah-col-classificacao":"=CLASSIFICAR(C2;>=81;\"● Superior/ Dificuldade\"@#EF9A9A;>=25;\"● Média Superior/ Adequado\"@#64B5F6;\"● Média/ Adequado\"@#A5D6A7)"}},
  {"id":"etdah-row-2","defaultValues":{"etdah-col-fator":"Aspectos Emocionais","etdah-col-ref":"25 a 80","etdah-col-classificacao":"=CLASSIFICAR(C3;>=81;\"● Superior/ Dificuldade\"@#EF9A9A;>=25;\"● Média Superior/ Adequado\"@#64B5F6;\"● Média/ Adequado\"@#A5D6A7)"}},
  {"id":"etdah-row-3","defaultValues":{"etdah-col-fator":"Autorregulação","etdah-col-ref":"25 a 80","etdah-col-classificacao":"=CLASSIFICAR(C4;>=81;\"● Superior/ Dificuldade\"@#EF9A9A;>=25;\"● Média Superior/ Adequado\"@#64B5F6;\"● Média/ Adequado\"@#A5D6A7)"}},
  {"id":"etdah-row-4","defaultValues":{"etdah-col-fator":"Hiperatividade","etdah-col-ref":"25 a 80","etdah-col-classificacao":"=CLASSIFICAR(C5;>=81;\"● Superior/ Dificuldade\"@#EF9A9A;>=25;\"● Média Superior/ Adequado\"@#64B5F6;\"● Média/ Adequado\"@#A5D6A7)"}}
]'::JSONB,
'[{"type":"p","children":[{"text":"Nota: ","bold":true,"italic":true},{"text":"A Autorregulação é analisada em três pontos: Atenção, Motivação e Ação"}]}]'::JSONB,
true);
