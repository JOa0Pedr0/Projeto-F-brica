# 🏭 Sistema de Gerenciamento de Fábrica (Factory Management System)

Uma API REST robusta desenvolvida em Java com Spring Boot para simular o gerenciamento de processos industriais, recursos e linha de produção.

> **Status do Projeto:** 🚀 Fase 4 Concluída (Migração completa para API REST)

## 📖 Sobre o Projeto

Este projeto nasceu como uma aplicação de console para estudo aprofundado de Orientação a Objetos e evoluiu para uma **API RESTful** completa. O objetivo principal foi simular o ecossistema de uma fábrica, gerenciando o ciclo de vida de ordens de produção, alocação de máquinas e gestão de funcionários (com hierarquia de cargos).

O diferencial deste projeto é a aplicação prática de conceitos sólidos de **Engenharia de Software**, saindo de persistência em memória para banco de dados relacional, e de interfaces CLI para endpoints HTTP modernos.

## 🛠 Tecnologias e Ferramentas

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3
* **Persistência:** Spring Data JPA / Hibernate
* **Banco de Dados:** H2 Database (Modo Arquivo/Embedded)
* **Gerenciamento de Dependências:** Maven
* **Testes:** JUnit 5 & Mockito
* **Logging:** SLF4J & Logback

## ✨ Principais Funcionalidades

### 1. Gerenciamento de Recursos (CRUD)
* **Máquinas:** Cadastro e controle de status (`OPERANDO`, `PARADA`, `EM_MANUTENCAO`).
* **Produtos:** Catálogo de produtos associados a especificações de máquinas.
* **Funcionários:** Sistema polimórfico para gestão de **Gerentes** e **Operadores de Máquina**.

### 2. Controle de Produção (Core Business)
* **Ordens de Produção:** Criação de ordens vinculando Produto, Quantidade, Máquina Real e Operador Responsável.
* **Ciclo de Vida:** Gerenciamento de estados da ordem via endpoints específicos:
    * `PENDENTE` ➡️ `EM_ANDAMENTO` ➡️ `CONCLUIDA`
    * Fluxo de cancelamento preservando histórico.

### 3. Regras de Negócio e Validações
* Validação de integridade: Não é possível remover máquinas/produtos que possuem histórico de produção.
* Validação de alocação: Operadores só podem assumir ordens se a máquina estiver `OPERANDO`.
* Prevenção de erros de tipo (Gerente vs. Operador) na alocação de tarefas.

### 4. Relatórios e Métricas
* Endpoints dedicados que retornam JSONs estruturados com métricas de produção, carga de trabalho pendente e alocação de recursos em tempo real.

## 🏗 Arquitetura do Projeto

O projeto segue uma arquitetura em camadas bem definida para garantir a separação de responsabilidades:

1.  **Controller Layer (`/controller`):** Exposição dos endpoints REST. Lida apenas com requisições HTTP e delega para o serviço.
2.  **Service Layer (`/service`):** O "cérebro" da aplicação. Contém toda a lógica de negócio, validações e orquestração de fluxo.
3.  **Repository Layer (`/dao`):** Interfaces `JpaRepository` para comunicação abstrata e eficiente com o banco de dados.
4.  **Domain Layer (`/entities`):** Classes POJO mapeadas com JPA (`@Entity`), utilizando Herança (`@Inheritance`) e Relacionamentos (`@ManyToOne`).

## 🚀 Como Rodar o Projeto

### Pré-requisitos
* Java JDK 21 instalado.
* Maven instalado (ou usar o wrapper).

### Passos
1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/JOa0Pedr0/Projeto-F-brica.git
    ```
2.  **Compile e baixe as dependências:**
    ```bash
    mvn clean install
    ```
3.  **Execute a aplicação:**
    Rode a classe principal `FabricaApiApplication.java` na sua IDE ou via terminal:
    ```bash
    mvn spring-boot:run
    ```

A API estará disponível em: `http://localhost:8080`

---

## 🔌 Documentação da API (Endpoints Principais)

### Máquinas
* `GET /api/maquinas` - Listar todas.
* `POST /api/maquinas` - Criar nova máquina.
* `PUT /api/maquinas/{id}` - Atualizar dados.
* `DELETE /api/maquinas/{id}` - Remover (se não houver histórico).

### Funcionários
* `POST /api/funcionarios/gerente` - Contratar Gerente.
* `POST /api/funcionarios/operador` - Contratar Operador.
* `GET /api/funcionarios` - Listar todos (polimórfico).

### Ordens de Produção
* `POST /api/ordens` - Criar nova ordem.
* `PUT /api/ordens/{id}/iniciar` - Mudar status para EM_ANDAMENTO.
* `PUT /api/ordens/{id}/concluir` - Mudar status para CONCLUIDA.
* `GET /api/ordens/relatorio` - Obter métricas e estatísticas.

---

## 🧪 Testes e Qualidade

O projeto conta com testes unitários utilizando **JUnit 5** e **Mockito** para garantir a integridade das regras de negócio na camada de Serviço, isolando dependências externas.

---

**Desenvolvido por João Pedro Guedes**