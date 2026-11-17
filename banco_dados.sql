CREATE DATABASE IF NOT EXISTS cadastro_pessoa;
USE cadastro_pessoa;

CREATE TABLE IF NOT EXISTS pessoa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    endereco VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL
);
CREATE TABLE IF NOT EXISTS pessoa_fisica (
    pessoa_id INT PRIMARY KEY,
    cpf VARCHAR(14) NOT NULL,
    FOREIGN KEY (pessoa_id) REFERENCES pessoa(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS pessoa_juridica (
    pessoa_id INT PRIMARY KEY,
    cnpj VARCHAR(18) NOT NULL,
    nome_fantasia VARCHAR(100),
    FOREIGN KEY (pessoa_id) REFERENCES pessoa(id) ON DELETE CASCADE
);

-- Tabelas novas: execute também em uma base já existente.
CREATE TABLE IF NOT EXISTS produto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    estoque INT NOT NULL DEFAULT 0,
    fornecedor_id INT NOT NULL,
    CHECK (preco > 0),
    CHECK (estoque >= 0),
    FOREIGN KEY (fornecedor_id) REFERENCES pessoa_juridica(pessoa_id)
);
CREATE TABLE IF NOT EXISTS venda (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data_venda DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cliente_id INT NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES pessoa_fisica(pessoa_id)
);
CREATE TABLE IF NOT EXISTS item_venda (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venda_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    valor_unitario DECIMAL(10,2) NOT NULL,
    CHECK (quantidade > 0),
    FOREIGN KEY (venda_id) REFERENCES venda(id) ON DELETE CASCADE,
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);
