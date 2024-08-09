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
