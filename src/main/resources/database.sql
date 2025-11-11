CREATE DATABASE clinica
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

CREATE TABLE especialidades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255)
);

CREATE TABLE medicos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    crm VARCHAR(20) NOT NULL UNIQUE,
    especialidade_id BIGINT NOT NULL,
    
    CONSTRAINT fk_medico_especialidade
        FOREIGN KEY (especialidade_id)
        REFERENCES especialidades(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);