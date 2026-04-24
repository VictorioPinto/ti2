--
-- PostgreSQL database dump para Wise Capital
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- -----------------------------------------------------
-- Estrutura das Tabelas
-- -----------------------------------------------------

-- Tabela: niveis_trilha
CREATE TABLE public.niveis_trilha (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descricao TEXT
);

-- Tabela: usuarios (Substituindo pontos por streak_days)
CREATE TABLE public.usuarios (
    id SERIAL PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    streak_days INTEGER DEFAULT 0, -- Sistema de ofensiva
    nivel_atual_id INTEGER DEFAULT 1 REFERENCES public.niveis_trilha(id),
    data_ultimo_acesso TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela: questionario_diagnostico (Para a IA analisar)
CREATE TABLE public.questionario_diagnostico (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER REFERENCES public.usuarios(id),
    respostas_json TEXT,
    nivel_sugerido_ia INTEGER REFERENCES public.niveis_trilha(id),
    data_realizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela: quizzes
CREATE TABLE public.quizzes (
    id SERIAL PRIMARY KEY,
    nivel_id INTEGER REFERENCES public.niveis_trilha(id),
    titulo VARCHAR(255) NOT NULL
);

-- Tabela: perguntas
CREATE TABLE public.perguntas (
    id SERIAL PRIMARY KEY,
    quiz_id INTEGER REFERENCES public.quizzes(id),
    pergunta TEXT NOT NULL,
    correta INTEGER NOT NULL,
    explicacao TEXT
);

-- Tabela: opcoes_pergunta
CREATE TABLE public.opcoes_pergunta (
    id SERIAL PRIMARY KEY,
    pergunta_id INTEGER REFERENCES public.perguntas(id),
    texto TEXT NOT NULL
);

-- Tabela: forum_topicos (Estilo Reddit)
CREATE TABLE public.forum_topicos (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER REFERENCES public.usuarios(id),
    titulo VARCHAR(255) NOT NULL,
    conteudo TEXT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela: forum_comentarios
CREATE TABLE public.forum_comentarios (
    id SERIAL PRIMARY KEY,
    topico_id INTEGER REFERENCES public.forum_topicos(id),
    usuario_id INTEGER REFERENCES public.usuarios(id),
    conteudo TEXT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    comentario_pai_id INTEGER -- Para respostas encadeadas
);

-- Tabela: investimentos (Dados vindos do db.json original)
CREATE TABLE public.investimentos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100),
    risco VARCHAR(20),
    retorno DECIMAL(5,2),
    categoria VARCHAR(50)
);

-- Tabela: faq
CREATE TABLE public.faq (
    id SERIAL PRIMARY KEY,
    pergunta TEXT,
    resposta TEXT
);

-- -----------------------------------------------------
-- Permissões
-- -----------------------------------------------------

-- Ajuste o 'ti2cc' para o seu nome de usuário do pgAdmin se for diferente
ALTER TABLE public.niveis_trilha OWNER TO ti2cc;
ALTER TABLE public.usuarios OWNER TO ti2cc;
ALTER TABLE public.questionario_diagnostico OWNER TO ti2cc;
ALTER TABLE public.quizzes OWNER TO ti2cc;
ALTER TABLE public.perguntas OWNER TO ti2cc;
ALTER TABLE public.opcoes_pergunta OWNER TO ti2cc;
ALTER TABLE public.forum_topicos OWNER TO ti2cc;
ALTER TABLE public.forum_comentarios OWNER TO ti2cc;
ALTER TABLE public.investimentos OWNER TO ti2cc;
ALTER TABLE public.faq OWNER TO ti2cc;

--
-- PostgreSQL database dump complete
--