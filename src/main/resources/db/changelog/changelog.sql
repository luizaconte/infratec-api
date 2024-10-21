-- liquibase formatted sql

-- changeset luiza:1 failOnError:false
-- preconditions onFail:MARK_RAN onError:HALT
-- comment: Logs gerados pelo sistema
CREATE TABLE tb_log
(
    id          bigserial NOT NULL,
    evento      varchar(100),
    ip          varchar(120),
    data_evento timestamp NOT NULL,
    mensagem    varchar(4096),
    CONSTRAINT tb_log_pk PRIMARY KEY (id)
);

-- changeset luiza:2 failOnError:false
-- preconditions onFail:MARK_RAN onError:HALT
-- comment: Usuários do sistema
CREATE TABLE tb_usuario
(
    id              serial       NOT NULL,
    nome            varchar(100) NOT NULL,
    login           varchar(30)  NOT NULL,
    senha           varchar(100) NOT NULL,
    email           varchar(100) NOT NULL,
    tipo            int          NOT NULL,
    data_inclusao   timestamp    NOT NULL,
    login_inclusao  varchar(30)  NOT NULL,
    data_alteracao  timestamp,
    login_alteracao varchar(30),
    CONSTRAINT tb_usuario_pk PRIMARY KEY (id)
);

-- changeset luiza:3 failOnError:false
-- preconditions onFail:MARK_RAN onError:HALT
-- comment: Chaves dos usuários
CREATE TABLE tb_usuario_chave
(
    id            serial       NOT NULL,
    id_usuario    int          NOT NULL,
    chave_publica varchar(128) NOT NULL,
    chave_privada varchar(128) NOT NULL,
    login         varchar(30)  NOT NULL,
    data_inclusao timestamp    NOT NULL,
    validade      bigint       NOT NULL,
    CONSTRAINT tb_usuario_chave_pk PRIMARY KEY (id)
);
ALTER TABLE tb_usuario_chave
    ADD CONSTRAINT fk_usuario FOREIGN KEY (id_usuario) REFERENCES tb_usuario (id);

-- changeset luiza:4 failOnError:false
-- preconditions onFail:MARK_RAN onError:HALT
-- comment: Departamentos
CREATE TABLE tb_departamento
(
    id   bigserial    NOT NULL,
    nome varchar(100) NOT NULL,
    CONSTRAINT tb_departamento_pk PRIMARY KEY (id)
);

-- changeset luiza:5 failOnError:false
-- comment: add cargo do usuário e chave do departamento
ALTER TABLE tb_usuario
    ADD cargo varchar(50);
ALTER TABLE tb_usuario
    ADD id_departamento int;
ALTER TABLE tb_usuario
    ADD CONSTRAINT fk_usuario_departamento FOREIGN KEY (id_departamento) REFERENCES tb_departamento (id);

-- changeset luiza:6 failOnError:false
-- comment: usuario e departamento admin
-- Insert para o departamento Administração
INSERT INTO tb_departamento (nome)
VALUES ('Administração');

-- Inserindo o usuário admin
INSERT INTO tb_usuario (nome, login, senha, email, tipo, data_inclusao, login_inclusao, cargo, id_departamento)
VALUES ('Administrador', -- Nome do usuário
        'admin', -- Login do usuário
        '$2a$10$pFk60XJnFVgBDkz2iNbNeua62IbAh9oF.4TwnndqJrs6V3t.eZdn6', -- Senha do usuário (não criptografada, isso deve ser feito em produção)
        'admin@example.com', -- Email do usuário
        0,
        CURRENT_TIMESTAMP, -- Data de inclusão
        'admin', -- Login de quem incluiu (pode ser o próprio admin para este exemplo)
        'Administrador', -- Cargo do usuário
        (SELECT id FROM tb_departamento WHERE nome = 'Administração') -- Id do departamento Administração
       );


-- changeset luiza:7 failOnError:false
-- preconditions onFail:MARK_RAN onError:HALT
-- comment: Chamados
CREATE TABLE tb_chamado
(
    id                     bigserial    NOT NULL,
    nome                   varchar(100) NOT NULL,
    descricao              varchar(500) NOT NULL,
    telefone               varchar(11)  NOT NULL,
    prioridade             int          NOT NULL,
    id_usuario_criacao     int          NOT NULL,
    id_usuario_responsavel int          NOT NULL,
    data_inclusao          timestamp    NOT NULL,
    data_alteracao         timestamp,
    CONSTRAINT tb_chamado_pk PRIMARY KEY (id)
);

ALTER TABLE tb_chamado
    ADD CONSTRAINT fk_chamado_usuario_criacao FOREIGN KEY (id_usuario_criacao) REFERENCES tb_usuario (id);
ALTER TABLE tb_chamado
    ADD CONSTRAINT fk_chamado_usuario_responsavel FOREIGN KEY (id_usuario_responsavel) REFERENCES tb_usuario (id);


-- changeset luiza:8 failOnError:false
-- comment: Comentarios do chamado
CREATE TABLE tb_comentario
(
    id             bigserial     NOT NULL,
    descricao      varchar(1000) NOT NULL,
    id_chamado     int           NOT NULL,
    data_inclusao  timestamp     NOT NULL,
    data_alteracao timestamp,
    CONSTRAINT tb_comen_pk PRIMARY KEY (id)
);

ALTER TABLE tb_comentario
    ADD CONSTRAINT fk_comentario_chamado FOREIGN KEY (id_chamado) REFERENCES tb_chamado (id);