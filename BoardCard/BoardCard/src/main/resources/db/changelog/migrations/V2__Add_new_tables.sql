-- Adiciona tabelas adicionais
CREATE TABLE IF NOT EXISTS cards (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    board_column_id BIGINT REFERENCES board_columns(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS blocks (
    id BIGSERIAL PRIMARY KEY,
    card_id BIGINT REFERENCES cards(id),
    blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    block_reason VARCHAR(500),
    unblocked_at TIMESTAMP,
    unblock_reason VARCHAR(500)
);