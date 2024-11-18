# bank-challenge

Este projeto é uma aplicação de gestão financeira simples construída com **Spring Boot** e **Thymeleaf**. Ele permite gerenciar contas bancárias, realizar transferências e autenticação de usuários.

---

## 🚀 **Como Baixar o Projeto**

1. Certifique-se de ter o **Git** instalado em sua máquina.
2. Execute o seguinte comando para clonar o repositório:

```bash
git clone https://github.com/ficheles/bank-challenge.git
```

3. Navegue até o diretório do projeto:

```bash
cd bank-challenge
```

## 🛠 Comandos para Executar o Projeto

### Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior
- Banco de Dados H2 configurado no projeto para testes iniciais

### Passos para Executar

1. Compile o projeto e baixe as dependências:

```bash
mvn clean install
```

2. Inicie a aplicação:

```bash
mvn spring-boot:run
```

3. Acesse a aplicação no navegador:

```bash
http://localhost:8080
```

### Utilizando Docker

#### Pré-requisitos

- Docker e Docker Compose instalados

**Passos**

1. Construa e inicie os containers:

   1.1 Este comando é necessario somente a primeira vez para criar a imagem docker

```bash
docker-compose up --build
```

2. Para executar os containers:

   2.1 Sempre que for utilizar a aplicação via docker

```bash
docker-compose up
```

3. Acesse a aplicação no navegador:

```bash
http://localhost:8080
```

**Docker Compose**

O arquivo docker-compose.yml inclui:

- Um container para a aplicação Java.
- Um container para o banco de dados PostgreSQL.

**Parar os Containers**

Para encerrar os serviços:

```bash
docker-compose down
```

## 🧪 Executando os Testes

Para rodar os testes unitários e de integração, utilize o comando abaixo:

```bash
mvn test
```

Os resultados dos testes serão exibidos no console, e o relatório pode ser encontrado na pasta `target/surefire-reports.`

## 📁 Estrutura do Projeto

- src/main/java: Contém o código fonte do projeto
- src/main/resources: Contém as configurações e templates Thymeleaf
- src/test/java: Contém os testes unitários e de integração

## 📄 Principais Funcionalidades

- Gerenciamento de Contas: Criação, edição e visualização de contas bancárias.
- Transferências: Realizar transferências entre contas.
- Autenticação: Login e controle de acesso com diferentes roles (ADMIN e USER).

## 👥 Contribuidores

Sinta-se à vontade para contribuir com melhorias no projeto! Faça um fork, crie um branch e envie um pull request.

```markdown
git checkout -b minha-feature
git commit -m "Minha nova funcionalidade"
git push origin minha-feature
```

## 📜 Licença

Este projeto está licenciado sob a MIT License.

```bash
git clone https://github.com/ficheles/bank-challenge.git .

```
