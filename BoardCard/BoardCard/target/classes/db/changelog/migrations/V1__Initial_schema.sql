-- Criação das tabelas básicas
CREATE TABLE IF NOT EXISTS boards (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS board_columns (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    board_id BIGINT REFERENCES boards(id),
    "order" INTEGER NOT NULL,
    kind VARCHAR(50) NOT NULL
);

-- ... outras tabelas iniciais