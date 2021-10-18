# Demo Flyway

## DORA: database change management

https://cloud.google.com/architecture/devops/devops-tech-database-change-management

* Establish effective communication of database changes
* Treat all database schema changes as migrations
* Zero-downtime database changes

## Lots of tooooools
* migrate (Go)
* alembic (Python)
* Active Record Migrations (Ruby on Rails)
* dbup (.NET)
* Entity Framework Migrations (.NET)
* Laravel Migrations (PHP)
* Flyway (platform-independent)
* Liquibase (platform-independent)

## UseCase: multiple dev, multiple env, shift-left dba
* Integrated into Spring-Boot
* Database migration in GIT
* Explicit script migration

## Demo: setup, app start

### Dependencies

```
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
</dependency>
```    

### Create data table

see `flyway-app\src\main\resources\db\migration\V00001__Create_user_table.sql`

### Add java code 

* Entity: User
* JPA: UserRepository 
* API: UserController
* Error handling: UserNotFoundException

### Start both database and application

```
docker compose up --build --detach
```

* Application is running on: `http://localhost:6868`
* MySql database is running on: `jdbc:mysql://localhost:3307/?user=root`

See `docker-compose.yml` file at the root level to explore containers definitions

## How it works: logs, migration files and tables

### Migration

* It checks a database schema to locate its metadata table (SCHEMA_VERSION by default). If the metadata table doesn't exist, it will create one.
* It scans an application classpath for available migrations.
* It compares migrations against the metadata table. If a version number is lower or equal to a version marked as current, it's ignored.
* It marks any remaining migrations as pending migrations. These are sorted based on the version number and are executed in order.
* As each migration is applied, the metadata table is updated accordingly.

### Logs

Check in logs, to find flyway migrating scripts in database, to See `docs\logs-start.txt`

```   
o.f.c.internal.license.VersionPrinter    : Flyway Community Edition 7.7.3 by Redgate
o.f.c.i.database.base.DatabaseType       : Database: jdbc:mysql://mysqldb:3306/flyway_db (MySQL 8.0)
o.f.core.internal.command.DbValidate     : Successfully validated 1 migration (execution time 00:00.026s)
o.f.core.internal.command.DbMigrate      : Current version of schema `flyway_db`: << Empty Schema >>
o.f.core.internal.command.DbMigrate      : Migrating schema `flyway_db` to version "00001 - Create user table"
o.f.c.i.s.DefaultSqlScriptExecutor       : DB: 'utf8' is currently an alias for the character set UTF8MB3, but will be an alias for UTF8MB4 in a fut
o.f.c.i.s.DefaultSqlScriptExecutor       : 0 rows affected
o.f.core.internal.command.DbMigrate      : Successfully applied 1 migration to schema `flyway_db`, now at version v00001 (execution time 00:00.138s)
```

### Migration file format

Flyway adheres to the following naming convention for migration scripts: *<Prefix><Version>__<Description>.sql*

Where:
<Prefix> – The default prefix is V, which we can change in the above configuration file using the flyway.sqlMigrationPrefix property.
<Version> – Migration version number. Major and minor versions may be separated by an underscore. The migration version should always start with 1.
<Description> – Textual description of the migration. A double underscore separates the description from the version numbers.
Example: V1_1_0__my_first_migration.sql

### Flyway migration table

* Migations scripts are stored in `flyway_schema_history` table, see `docs\show-tables.txt`
* Table `flyway_schema_history` keep execution date, description, success, see  `docs\flyway-table.txt`

## UseCases

### database initialization

Let's populate first data via Flyway !

see `flyway-app\src\main\resources\db\migration\V00002__Insert_users.sql`

```
INSERT INTO users (name) VALUES ('Bilbo Baggins'),('Frodo Baggins');
```

Then reload application container only 

```
docker compose up --build --detach app
```

Check database content, table `users` content and table `flyway_schema_history` content, if you restart app flyway will not re-insert users, easy data insertion.

### db migration (Add new column schema)

Add a new script in migration folder to add new fields

see `flyway-app\src\main\resources\db\migration\V00003__Alter_user_add_firstname.sql`

```
/* Add new columns and update column with content */
ALTER TABLE users ADD country varchar(50), ADD age int not null;
 
UPDATE users SET age=5;
```

Update user class with new fields: User.java

```
  private String country;
  private Long age;
```

Then reload application container only 

```
docker compose up --build --detach app
```

Check database content, table `users` has new columns and `age` is updated to 5 for all users.

### Restart from scratch, no ?

Ok, let's simulate that host is a new environment, remove containers and database volumes. 

```
docker compose down
docker volume rm flyway-demo_db
```

Create and start containers, then check database content !

```
docker compose up
```

Wunderbar, tables and contents is setup and everything has been reset !

## FAQ
* Why not simply use spring jpa "auto-update" ?
* SQL script migration only ?
* If migration failed ?