# 🏥 Sistema de Gestão de Clínicas Médicas — **AgendaClinic**

O **AgendaClinic** é um sistema desktop desenvolvido para a administração completa de uma clínica médica, integrando informações, agilizando processos (agendamentos, prontuários e cadastros) e garantindo segurança no armazenamento dos dados.  
O projeto substitui o modelo manual, reduzindo atrasos no atendimento e eliminando riscos de perda de histórico clínico.

O desenvolvimento aplicou princípios de **Engenharia de Software**, **POO**, **padrões de projeto** e metodologia de **Pesquisa Aplicada**.

---

## 💻 Tecnologias Utilizadas

O sistema utiliza uma pilha de tecnologias robustas:

| Categoria | Tecnologia | Detalhes |
|----------|------------|----------|
| Linguagem | **Java** | JDK 21 (ou superior) |
| Interface Gráfica | **Java Swing** | Uso de `JFrame`, `JPanel`, `JTable` |
| Persistência | **MySQL Server** | Banco relacional para armazenamento seguro |
| Conectividade | **JDBC** | Isolamento da lógica de conexão |
| Controle de Versão | **Git/GitHub** | Gestão do código |

---

## 🧱 Arquitetura e Padrões de Projeto

O sistema segue uma arquitetura em camadas, garantindo organização, modularidade e fácil manutenção.

### 🧩 **Padrões Aplicados**

| Padrão | Camada(s) | Descrição |
|--------|------------|-----------|
| **MVC — Model, View, Controller** | Entidades, Screens, Services | Separa dados, regras de negócio e UI |
| **DAO (Data Access Object)** | DAOs | Encapsula toda a comunicação com o MySQL via JDBC |
| **Singleton** | SessionContext | Garante uma única instância do contexto do usuário logado |

---

## 📁 Estrutura de Pacotes

| Pacote | Camada | Responsabilidade |
|--------|--------|------------------|
| `br.edu.imepac.clinica.entidades` | Model | Entidades (Paciente, Medico, Consulta, BaseEntity) |
| `br.edu.imepac.clinica.daos` | DAO | CRUDs com JDBC usando BaseDao |
| `br.edu.imepac.clinica.services` | Regras de Negócio | AuthService, ConsultaService, validações e fluxo |
| `br.edu.imepac.clinica.screens` | View | Telas Swing organizadas por módulos |
| `br.edu.imepac.clinica.session` | Segurança | SessionContext (Singleton) |
| `br.edu.imepac.clinica.exceptions` | Infra | Exceções personalizadas (ValidationException etc.) |

---

## 🚀 Funcionalidades Principais

### 1️⃣ **Autenticação e Controle de Acesso**

- Login com usuário/senha através do **AuthService**
- Verifica se o usuário está **ATIVO** e não **BLOQUEADO**
- **SessionContext** guarda:
  - Usuário logado  
  - Perfil  
  - Funcionalidades liberadas
- O menu principal habilita/desabilita itens conforme permissões

---

### 2️⃣ **Gestão de Agenda (ConsultaService)**

Funções principais:
- Criar consulta (obrigatório: **data/hora**, **médico**, **paciente**)
- Impede horários conflitantes para o mesmo médico
- Consultas iniciam com **status AGENDADA**
- Remarcações:
  - Proibido remarcar CANCELADA ou REALIZADA
  - Ao remarcar, retorna para **AGENDADA**
- Médicos visualizam consultas:
  - De hoje em diante
  - Por dia específico

---

### 3️⃣ **Cadastros do Sistema**

#### Médico
- CRUD completo  
- **Exclusão real é bloqueada** caso existam consultas vinculadas  
- Nestes casos, o médico é **inativado**

#### Convênio
- Mesma lógica dos médicos  
- Consulta vinculada → desativação ao invés de exclusão

#### Especialidade
- Exclusão bloqueada se houver médicos usando a especialidade  
- Lança `ValidationException`

---

### 4️⃣ **Prontuários e Histórico (ProntuarioService)**

#### Atendimento Médico
- Iniciado a partir da tela de agenda
- Preenchimento do prontuário pelo médico

#### Regras
- Campo **Resumo** é obrigatório
- Após salvar:
  - Atualiza ou cria prontuário
  - Consulta muda para **REALIZADA**

#### Histórico do Paciente
- Tela exibe todos os prontuários do paciente
- Ordenados da consulta mais recente para a mais antiga

---

## 📌 Conclusão

O **AgendaClinic** oferece uma solução completa para clínicas médicas, com foco em segurança, organização, usabilidade e boas práticas de engenharia de software.

---
