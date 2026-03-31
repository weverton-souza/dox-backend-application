CREATE TABLE reference_entries (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    text       TEXT         NOT NULL,
    instrument VARCHAR(200),
    authors    VARCHAR(500),
    year       INTEGER,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

INSERT INTO reference_entries (text, instrument, authors, year) VALUES
('BRIÃO, J. C.; CAMPANHOLO, K. R. Funções executivas. In: MIOTTO, E. C. et al. Manual de Avaliação Neuropsicológica. São Paulo: Memnon, 2018. v. 1.', 'Funções Executivas', 'Brião, Campanholo', 2018),
('CARDOSO, C. O. et al. Funções executivas e regulação emocional: intervenções e implicações educacionais. In: DIAS, N. M.; MECCA, T. Contribuições da neuropsicologia e da psicologia para intervenção no contexto educacional. São Paulo: Memnon, 2015.', 'Funções Executivas', 'Cardoso, Pureza, Gonçalves, Fonseca', 2015),
('D''PAULA, J. J.; MALLOY-DINIZ, L. F. Teste de Aprendizagem Auditivo-Verbal de Rey – RAVLT. São Paulo: Vetor, 2018.', 'RAVLT', 'D''Paula, Malloy-Diniz', 2018),
('DIAMOND, A. Executive functions. Annual Review of Psychology, v. 64, p. 135-168, 2013.', 'Funções Executivas', 'Diamond', 2013),
('GRUBER, C. P.; CONSTANTINO, J. N. Escala de Responsividade Social – SRS-2. São Paulo: Hogrefe CETEPP, 2021.', 'SRS-2', 'Gruber, Constantino', 2021),
('MALLOY-DINIZ, L. F. et al. Avaliação neuropsicológica. Porto Alegre: Artmed, 2010.', 'Avaliação Neuropsicológica', 'Malloy-Diniz', 2010),
('MALLOY-DINIZ, L. F. et al. Neuropsicologia das funções executivas e da atenção. In: FUENTES, D. et al. (Org.). Neuropsicologia: Teoria e Prática. 2. ed. Porto Alegre: Artmed, 2014.', 'Funções Executivas', 'Malloy-Diniz, De Paula, Sedó, Fuentes, Leite', 2014),
('PESSOTTO, F.; BARTHOLOMEU, D. Guia prático das Escalas Wechsler: uso e análise das escalas WISCIV, WAIS-III e WASI. São Paulo: Pearson Clinical Brasil, 2019.', 'WAIS-III, WISCIV, WASI', 'Pessotto, Bartholomeu', 2019),
('PINKER, S. O Instinto da Linguagem: como a mente cria a linguagem. São Paulo: Editora Martins Fontes, 2002.', 'Linguagem', 'Pinker', 2002),
('PINKER, S. Do que é feito o pensamento: a língua como janela para a natureza humana. São Paulo: Companhia das Letras, 2008.', 'Linguagem', 'Pinker', 2008),
('REY, A. Figuras Complexas de Rey – Teste de Cópia e de Reprodução de Memória de Figuras Geométricas Complexas. 2. ed. São Paulo: Pearson, 2014.', 'Figuras Complexas de Rey', 'Rey', 2014),
('SEDÓ, M.; PAULA, J. J.; MALLOY-DINIZ, L. F. O teste dos cinco dígitos – FDT – Five Digit Test. São Paulo: Hogrefe CETEPP, 2015.', 'FDT', 'Sedó, Paula, Malloy-Diniz', 2015),
('SERPA, A. et al. Torre de Londres – ToL-BR. São Paulo: Vetor, 2023.', 'ToL-BR', 'Serpa, Timóteo, Oliveira, Querino, Malloy-Diniz', 2023),
('WECHSLER, D. Escala de Inteligência Wechsler para Adultos 3ª. Edição – WAIS III. São Paulo: Pearson, 2017.', 'WAIS-III', 'Wechsler', 2017),
('BENCZIK, E. B. P. Escala de Avaliação de Transtorno de Déficit de Atenção e Hiperatividade – Versão Adolescentes e Adultos – ETDAH-AD. 1. ed. São Paulo: Vetor, 2013.', 'ETDAH-AD', 'Benczik', 2013),
('HU, W.; ADEY, P. Avaliação de traços, processos cognitivos e produtos científico-criativos. 2002.', 'Criatividade', 'Hu, Adey', 2002),
('ALVES, I. C. B.; LEMES, L. S.; RABELO, I. S. Inventário Fatorial de Personalidade – IFP-II. São Paulo: Editora Pearson, 2019.', 'IFP-II', 'Alves, Lemes, Rabelo', 2019),
('RUEDA, F. J. M. Bateria Psicológica para Avaliação da Atenção – BPA-2. São Paulo: Vetor, 2022.', 'BPA-2', 'Rueda', 2022),
('BARTHOLOMEU, D.; PESSOTTO, F. et al. Teste dos Cinco Pontos. São Paulo: Pearson, 2019.', 'Teste dos Cinco Pontos', 'Bartholomeu, Pessotto', 2019);
