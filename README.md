# BoardCard

## Visão Geral

O BoardCard é uma aplicação Spring Boot desenvolvida para gerenciar um quadro de tarefas (Kanban ou similar). Permite a criação de boards, colunas (com diferentes tipos como "A Fazer", "Em Progresso", "Concluído", "Cancelado"), e cards (tarefas) que podem ser movidos entre as colunas. O sistema também oferece funcionalidades para bloquear/desbloquear e cancelar cards, além de visualizar o estado do board, colunas e cards individualmente.

Este projeto serve como um exemplo prático de desenvolvimento de aplicações backend com Java, Spring Boot, JPA/Hibernate e um banco de dados H2 em memória (configurável).

## Funcionalidades Principais

* **Criação de Boards:** Permite a criação de quadros de tarefas com um nome.
* **Criação de Colunas:** Adição de colunas aos boards, com nome, ordem e tipo (Inicial, Pendente, Final, Cancelado).
* **Criação de Cards:** Criação de tarefas (cards) com título e descrição, inicialmente alocadas à coluna inicial do board.
* **Movimentação de Cards:** Permite mover cards para a próxima coluna disponível no fluxo do board.
* **Bloqueio de Cards:** Possibilidade de bloquear um card, impedindo sua movimentação, com um motivo.
* **Desbloqueio de Cards:** Permite desbloquear um card, tornando-o novamente movimentável, com um motivo.
* **Cancelamento de Cards:** Move um card para a coluna de cancelamento do board.
* **Visualização do Board:** Exibe o nome do board e o número de cards em cada coluna.
* **Visualização da Coluna:** Mostra o nome, tipo e a lista de cards presentes em uma coluna específica.
* **Visualização do Card:** Detalhes de um card específico, incluindo título, descrição, estado de bloqueio, motivo do bloqueio e a coluna atual.

## Tecnologias Utilizadas

* **Java:** Linguagem de programação principal.
* **Spring Boot:** Framework para facilitar a criação de aplicações Java standalone e de nível empresarial.
* **Spring Data JPA:** Simplifica o acesso e a persistência de dados com JPA (Java Persistence API) e Hibernate.
* **Hibernate:** Framework ORM (Object-Relational Mapping) utilizado pelo Spring Data JPA.
* **H2 Database:** Banco de dados relacional em memória utilizado para desenvolvimento e testes (configurável para outros bancos de dados).
* **Lombok:** Biblioteca Java para reduzir o código boilerplate (getters, setters, construtores, etc.) através de anotações.
* **Maven:** Ferramenta de gerenciamento de dependências e build.

## Pré-requisitos

* **Java Development Kit (JDK):** Versão 17 ou superior.
* **Maven:** Versão 3.6.0 ou superior.
* **IntelliJ IDEA (ou outra IDE Java):** Recomendado para desenvolvimento, mas não obrigatório para execução.

## Como Executar

1.  **Clone o repositório:**
    ```bash
    git clone (https://github.com/DanniNascimento/BoardCard)
    cd BoardCard/BoardCard/BoardCard # Navegue até a pasta raiz do projeto
    ```

2.  **Construa o projeto com Maven:**
    ```bash
    mvn clean install
    ```

3.  **Execute a aplicação Spring Boot:**
    Você pode executar a aplicação de duas maneiras:

    * **Pela linha de comando Maven:**
        ```bash
        mvn spring-boot:run
        ```

    * **Pela sua IDE (IntelliJ IDEA, Eclipse, etc.):** Localize a classe principal `com.danni.BoardCard.Main` e execute-a.

4.  **Acessando o Console H2 (Opcional):**
    Se você quiser visualizar o banco de dados H2 em memória durante a execução, acesse o seguinte endereço no seu navegador:
    ```
    http://localhost:8080/h2-console
    ```
    Use as seguintes configurações (geralmente as padrões):
    * **JDBC URL:** `jdbc:h2:mem:boarddb`
    * **User Name:** `POSTGRES`
    * **Password:** (deixe em branco)

    **Atenção:** O console H2 só estará acessível enquanto a aplicação estiver rodando.

## Como Usar (Interface de Linha de Comando)

A aplicação oferece uma interface de linha de comando interativa para demonstrar suas funcionalidades. Ao executar a aplicação, você verá um menu com as seguintes opções:
