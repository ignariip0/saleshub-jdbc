# saleshub-jdbc

Sistema de vendedores e departamentos feito com **JDBC puro e padrão DAO**, sem framework.

O projeto nasceu no módulo de JDBC do curso de Java do Nélio Alves. Escrevi ele pra entender o que acontece por baixo antes de ir pro JPA/Hibernate e pro Spring Data: abrir conexão, montar `PreparedStatement`, ler `ResultSet` linha por linha e transformar tudo isso em objeto.

## Stack

- Java 25 (Zulu): o `Program` usa `static void main` sem `public`, que só compila a partir do 25
- MySQL
- JDBC com MySQL Connector/J 9.7.0
- IntelliJ IDEA

## O que tem

**CRUD completo** pras duas entidades:

| Operação | `SellerDao` | `DepartmentDao` |
|---|---|---|
| `insert` (com retorno do Id gerado) | ✅ | ✅ |
| `update` | ✅ | ✅ |
| `deleteById` | ✅ | ✅ |
| `findById` | ✅ | ✅ |
| `findAll` | ✅ | ✅ |
| `findByDepartment` | ✅ | — |

**Decisões que valem destacar:**

- **Interface + implementação separadas** → `SellerDao` e `DepartmentDao` definem o contrato e `SellerDaoJDBC`/`DepartmentDaoJDBC` implementam com JDBC. Quem usa o DAO não sabe que por trás é JDBC.
- **`DaoFactory`** → é o único ponto que cria os DAOs e injeta a conexão. O resto do código depende só da interface.
- **JOIN entre `seller` e `department`** → nas três buscas de vendedor, o department vem na mesma query, sem uma segunda consulta por vendedor.
- **`Map<Integer, Department>` no `findAll` e no `findByDepartment`** → evita criar um objeto `Department` repetido pra cada linha. Vendedores do mesmo departamento apontam pra mesma instância.
- **Exceções próprias** → `DbException` encapsula a `SQLException` (checked) numa runtime exception e guarda a original como causa, então o stack trace mostra o `Caused by:` com o erro real do MySQL. `DbIntegrityException` é lançada no `deleteById` de department quando o MySQL devolve o erro 1451 (departamento que ainda tem vendedores, violação de FK).
- **Gerenciamento de recursos** → `Statement` e `ResultSet` são declarados antes do `try` e fechados no `finally` de cada método, então fecham mesmo quando dá erro. A conexão é uma só, estática, guardada na classe `DB`, e fica aberta enquanto a aplicação roda.

## Estrutura

```
src
├── application
│   ├── Program        → testes do SellerDao
│   └── Program2       → testes do DepartmentDao
├── db
│   ├── DB             → abre/fecha conexão e lê db.properties
│   ├── DbException
│   └── DbIntegrityException
└── model
    ├── dao
    │   ├── DaoFactory
    │   ├── SellerDao
    │   ├── DepartmentDao
    │   └── impl
    │       ├── SellerDaoJDBC
    │       └── DepartmentDaoJDBC
    └── entities
        ├── Seller
        └── Department
```

## Como rodar

**1. Criar o banco**

```sql
DROP DATABASE IF EXISTS coursejdbc;
CREATE DATABASE coursejdbc;
USE coursejdbc;

CREATE TABLE department (
  Id   INT NOT NULL AUTO_INCREMENT,
  Name VARCHAR(60) DEFAULT NULL,
  PRIMARY KEY (Id)
);

CREATE TABLE seller (
  Id           INT NOT NULL AUTO_INCREMENT,
  Name         VARCHAR(60) NOT NULL,
  Email        VARCHAR(100) NOT NULL,
  BirthDate    DATETIME NOT NULL,
  BaseSalary   DOUBLE NOT NULL,
  DepartmentId INT NOT NULL,
  PRIMARY KEY (Id),
  FOREIGN KEY (DepartmentId) REFERENCES department (Id)
);

INSERT INTO department (Name) VALUES ('Computers'), ('Electronics'), ('Fashion'), ('Books');

INSERT INTO seller (Name, Email, BirthDate, BaseSalary, DepartmentId) VALUES
  ('Bob Brown',     'bob@gmail.com',     '1998-04-21', 1000, 1),
  ('Maria Green',   'maria@gmail.com',   '1979-12-31', 3500, 2),
  ('Alex Grey',     'alex@gmail.com',    '1988-01-15', 2200, 1),
  ('Martha Red',    'martha@gmail.com',  '1993-11-30', 3000, 4),
  ('Donald Blue',   'donald@gmail.com',  '2000-01-09', 4000, 3),
  ('Alex Pink',     'bob@gmail.com',     '1997-03-04', 3000, 2),
  ('Anna Purple',   'anna@gmail.com',    '1995-06-18', 2800, 2);
```

**2. Criar o `db.properties` na raiz do projeto**

Ele está no `.gitignore` porque guarda credencial local.

```properties
user=root
password=sua_senha
dburl=jdbc:mysql://localhost:3306/coursejdbc
sslMode=DISABLED
```

**3. Adicionar o driver**

Baixe o MySQL Connector/J e adicione o `.jar` como library do projeto. No IntelliJ: *File → Project Structure → Libraries → + → Java*.

**4. Rodar**

- `Program` → testa o `SellerDao` (o teste de delete pede um Id no console)
- `Program2` → testa o `DepartmentDao`

Os testes alteram o banco: cada execução do `Program` insere um vendedor "Greg" e renomeia o seller 1 pra "Martha Wayne", e o `Program2` renomeia o department 1 pra "Computers Updated". Pra voltar ao estado inicial, rode o script de novo.

## Status

Projeto concluído. Ele cumpriu o papel dele: me fazer entender a camada de acesso a dados na mão. O próximo passo foi levar o mesmo raciocínio pro Spring Boot com JPA.
