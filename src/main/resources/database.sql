-- 1. ESPECIALIDADE
CREATE TABLE especialidade (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    area VARCHAR(100),
    descricao VARCHAR(255)
);

-- 2. MEDICO
CREATE TABLE medico (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    telefone VARCHAR(20),
    crm VARCHAR(20) NOT NULL,
    tempo_experiencia INT,
    formacao VARCHAR(255),
    especialidade_id BIGINT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_medico_especialidade
        FOREIGN KEY (especialidade_id) REFERENCES especialidade(id)
);

-- 3. CONVENIO
CREATE TABLE convenio (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    codigo VARCHAR(50) NOT NULL,
    descricao VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- 4. PACIENTE
CREATE TABLE paciente (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    telefone VARCHAR(20),
    plano_saude VARCHAR(100),
    tipo_sanguineo VARCHAR(5),
    data_nascimento DATE,
    sexo VARCHAR(10),
    convenio_id BIGINT,
    CONSTRAINT fk_paciente_convenio
        FOREIGN KEY (convenio_id) REFERENCES convenio(id)
);

-- 5. SECRETARIA
CREATE TABLE secretaria (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    telefone VARCHAR(20),
    pis VARCHAR(20) NOT NULL,
    turno VARCHAR(30),
    setor VARCHAR(50)
);

-- 6. PERFIL
CREATE TABLE perfil (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    nivel_acesso INT NOT NULL
);

-- 7. PERFIL_FUNCIONALIDADE
CREATE TABLE perfil_funcionalidade (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    perfil_id BIGINT NOT NULL,
    funcionalidade VARCHAR(100) NOT NULL,
    CONSTRAINT fk_pf_perfil
        FOREIGN KEY (perfil_id) REFERENCES perfil(id)
);

-- 8. USUARIO
CREATE TABLE usuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome_login VARCHAR(100) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    ultimo_login DATETIME,
    bloqueado BOOLEAN NOT NULL DEFAULT FALSE,
    tentativas_falhas INT NOT NULL DEFAULT 0,
    secretaria_id BIGINT,
    perfil_id BIGINT,
    CONSTRAINT fk_usuario_secretaria
        FOREIGN KEY (secretaria_id) REFERENCES secretaria(id),
    CONSTRAINT fk_usuario_perfil
        FOREIGN KEY (perfil_id) REFERENCES perfil(id)
);

-- 9. CONSULTA
CREATE TABLE consulta (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    data_hora DATETIME NOT NULL,
    retorno BOOLEAN NOT NULL DEFAULT FALSE,
    carteira_convenio VARCHAR(100),
    observacao VARCHAR(255),
    status VARCHAR(30) NOT NULL,
    paciente_id BIGINT NOT NULL,
    medico_id BIGINT NOT NULL,
    convenio_id BIGINT,
    CONSTRAINT fk_consulta_paciente
        FOREIGN KEY (paciente_id) REFERENCES paciente(id),
    CONSTRAINT fk_consulta_medico
        FOREIGN KEY (medico_id) REFERENCES medico(id),
    CONSTRAINT fk_consulta_convenio
        FOREIGN KEY (convenio_id) REFERENCES convenio(id)
);

-- 10. PRONTUARIO
CREATE TABLE prontuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    resumo VARCHAR(255) NOT NULL,
    anotacoes TEXT,
    arquivo_pdf VARCHAR(255),
    consulta_id BIGINT NOT NULL,
    CONSTRAINT fk_prontuario_consulta
        FOREIGN KEY (consulta_id) REFERENCES consulta(id)
);