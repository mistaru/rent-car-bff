# Rent Car Application


### Технологический стек

- **Java 17**, **Spring Boot 2.7.18**
- **PostgreSQL 15** (docker-compose)
- **Spring Security** — stateless, JWT-токены (через `TokenAuthFilter`)
- **Hibernate ddl-auto: update** — схема создаётся автоматически из entity-классов


## Локальный запуск

1. Создание БД - выполнить `INITIAL_DB_CREATION.sql` в PostgreSQL (или запустить `docker-compose up` для автоматического создания БД через Docker)
```bash
docker-compose up -d
```
Поднимается PostgreSQL 15 на localhost:5432:

DB: rent_car
User: postgres
Password: postgres

2. Запустить бэкенд
```bash
./mvnw clean install -DskipTests
cd bff
../mvnw spring-boot:run
```
Сервер запустится на **http://localhost:8081**

## ⚙️ Конфигурация

| Файл | Описание |
|------|----------|
| `core/src/main/resources/application.yml` | Общие настройки: порт 8081, JPA, Redis-кеш, liquibase off |
| `core/src/main/resources/application-test.yml` | Профиль `test` (по умолчанию): PostgreSQL на localhost |
| `core/src/main/resources/application-dev.yml` | Профиль `dev` |
| `core/src/main/resources/application-prod.yml` | Профиль `prod` |

**Активный профиль:** `test` (по умолчанию, через `ACTIVE_PROFILE` env).


3. Выполнить SQL-скрипты `001_AUTH_SEED.sql` и `002_RENTAL_SEED.sql` для заполнения БД начальными данными (пользователи, роли, автомобили и т.д.)

