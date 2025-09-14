CREATE TABLE material (
    id               BIGSERIAL PRIMARY KEY,
    nome             VARCHAR(50) NOT NULL,
    codigo_material  VARCHAR(20) NOT NULL UNIQUE,
    tipo             VARCHAR(20) NOT NULL
);

CREATE TABLE centro_custo (
    id                   BIGSERIAL PRIMARY KEY,
    nome                 VARCHAR(50) NOT NULL,
    codigo_centro_custo  VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE movimentacao_material (
    id                        BIGSERIAL PRIMARY KEY,
    material_id               BIGINT NOT NULL REFERENCES material(id),
    centro_origem_id          BIGINT REFERENCES centro_custo(id),
    centro_destino_id         BIGINT REFERENCES centro_custo(id),
    quantidade_movimentada    BIGINT NOT NULL,
    tipo                      VARCHAR(20) NOT NULL,
    data                      TIMESTAMP NOT NULL DEFAULT NOW(),
    observacao                TEXT,

    CONSTRAINT ck_mov_tipo      CHECK (tipo IN ('ENTRADA','SAIDA','TRANSFERENCIA')),
    CONSTRAINT ck_mov_qtd_pos   CHECK (quantidade_movimentada > 0),

    -- regras de nulidade por tipo
    CONSTRAINT ck_mov_entrada   CHECK (
        (tipo = 'ENTRADA' AND centro_origem_id IS NULL AND centro_destino_id IS NOT NULL)
        OR tipo <> 'ENTRADA'
    ),
    CONSTRAINT ck_mov_saida     CHECK (
        (tipo = 'SAIDA' AND centro_origem_id IS NOT NULL AND centro_destino_id IS NULL)
        OR tipo <> 'SAIDA'
    ),
    CONSTRAINT ck_mov_transf    CHECK (
        (tipo = 'TRANSFERENCIA' AND centro_origem_id IS NOT NULL AND centro_destino_id IS NOT NULL AND centro_origem_id <> centro_destino_id)
        OR tipo <> 'TRANSFERENCIA'
    )
);

CREATE TABLE reserva_estoque (
    id                     BIGSERIAL PRIMARY KEY,
    material_id            BIGINT NOT NULL REFERENCES material(id),
    centro_origem_id       BIGINT NOT NULL REFERENCES centro_custo(id),
    centro_destino_id      BIGINT NOT NULL REFERENCES centro_custo(id),
    quantidade_solicitada  BIGINT NOT NULL,
    quantidade_atendida    BIGINT NOT NULL DEFAULT 0,
    status                 VARCHAR(20) NOT NULL,
    data_aprovacao         TIMESTAMP,
    data_atendimento       TIMESTAMP,

    CONSTRAINT ck_reserva_qtds   CHECK (quantidade_solicitada > 0 AND quantidade_atendida >= 0 AND quantidade_atendida <= quantidade_solicitada),
    CONSTRAINT ck_reserva_status CHECK (status IN ('ABERTA','APROVADA','ATENDIDA','CANCELADA'))
);

CREATE TABLE ordem_producao (
    id                     BIGSERIAL PRIMARY KEY,
    codigo_producao        VARCHAR(50) NOT NULL UNIQUE,
    material_id            BIGINT NOT NULL REFERENCES material(id),
    centro_id              BIGINT NOT NULL REFERENCES centro_custo(id),
    quantidade_planejada   BIGINT NOT NULL,
    quantidade_concluida   BIGINT NOT NULL DEFAULT 0,
    status                 VARCHAR(20) NOT NULL,
    data_abertura          TIMESTAMP NOT NULL DEFAULT NOW(),
    data_fechamento        TIMESTAMP,

    CONSTRAINT ck_op_qtds    CHECK (quantidade_planejada > 0 AND quantidade_concluida >= 0 AND quantidade_concluida <= quantidade_planejada),
    CONSTRAINT ck_op_status  CHECK (status IN ('LIBERADA','EM_ANDAMENTO','CONCLUIDA'))
);

CREATE TABLE estoque_centro_custo (
    id              BIGSERIAL PRIMARY KEY,
    centro_custo_id BIGINT NOT NULL REFERENCES centro_custo(id),
    material_id     BIGINT NOT NULL REFERENCES material(id),
    saldo           BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uq_estoque_centro_material UNIQUE (centro_custo_id, material_id),
    CONSTRAINT ck_saldo_nao_negativo CHECK (saldo >= 0)
);

CREATE INDEX idx_mov_material        ON movimentacao_material (material_id);
CREATE INDEX idx_mov_origem          ON movimentacao_material (centro_origem_id);
CREATE INDEX idx_mov_destino         ON movimentacao_material (centro_destino_id);
CREATE INDEX idx_reserva_status      ON reserva_estoque (status);
CREATE INDEX idx_op_status           ON ordem_producao (status);
