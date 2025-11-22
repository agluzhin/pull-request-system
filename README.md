# Pull Request System

---
**Описание:**
> Spring Boot in-memory приложение для работы с pull request'ами в группах.
---
## Содержание
* [Технологии](#технологии)
* [Структура проекта](#структура-проекта)
* [Запуск](#запуск)
    * [Локальный запуск](#локальный-запуск)
    * [Docker](#docker)
    * [Docker Compose](#docker-compose)
    * [Makefile](#makefile)
* [API Endpoints](#api-endpoints)
* [Примечания](#примечания)
---
## Технологии
* Java 17
* Spring Boot 3.4.12
* Database (In-Memory - HashMaps)
* Maven
* Docker
* Docker Compose
* Makefile
---
## Структура проекта
```
project-root/
├─ src/
│  ├─ main/
│  │  ├─ java/com/example/app/agluzhin.pull_request_system
│  │  │  ├─ core
│  │  │  │  ├─ controllers
│  │  │  │  ├─ enums
│  │  │  │  ├─ generators
│  │  │  │  ├─ handlers
│  │  │  │  ├─ models
│  │  │  │  ├─ services
│  │  │  │  └─ utils
│  │  │  └─ PullRequestSystemApplication.java
│  │  └─ resources/
│  │     ├─ componenets/schemas/
│  │     │  ├─ ErrorResponse.json
│  │     │  ├─ PullRequest.json
│  │     │  ├─ PullRequestShort.json
│  │     │  └─ and etc.
│  │     ├─ static
│  │     ├─ templates
│  │     └─ application.properties
│  └─ test/
├─ Dockerfile
├─ docker-compose.yml
├─ Makefile
├─ agluzhin.pull_request_system.postman_collection.json
├─ mvmw
├─ mvmw.cmd
├─ pom.xml 
└─ README.md
```
---
## Запуск

### Локальный запуск

Сборка проекта (шаг 1):
```bash
./mvnw clean package
```
Запуск приложения (шаг 2):
```bash
java -jar target/app.jar
```
Приложение будет доступно на `http://localhost:8080`.

---
### Docker
Сборка и запуск контейнера:
```bash
docker build -t pull-request-system .
docker run -p 8080:8080 pull-request-system
```
**Dockerfile (содержание):**
```dockerfile
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```
---
### Docker Compose
Запуск через Docker Compose:
```bash
docker-compose up --build
```
**docker-compose.yml (содержание):**
```yaml
services:
  backend:
    build: .
    container_name: pull-request-system
    ports:
      - "8080:8080"
    restart: unless-stopped
```
---
### Makefile

Содержание:

```makefile
IMAGE_NAME = pull-request-system
CONTAINER_NAME = pull-request-system-backend

build:
	@echo "Сборка проекта и создание Docker-образа."
	mvnw.cmd clean package -DskipTests
	docker build -t $(IMAGE_NAME)

up:
	@echo "Запуск приложения через Docker Compose."
	docker compose up --build

down:
	@echo "Остановка и удаление контейнеров."
	docker compose down

clean:
	@echo "Очистка Docker-ресурсов."
	docker system prune -f

logs:
	docker compose logs -f

purge:
	@echo "Полная очистка образа и контейнера."
	-docker rm -f $(CONTAINER_NAME) || true
	-docker rmi -f $(IMAGE_NAME) || true

```
---
## API Endpoints

Документация API (согласно предоставленной спецификации):

| Метод | Endpoint              | Описание                                                               |
|-------|-----------------------|------------------------------------------------------------------------|
| POST  | /team/add             | Создать команду с участниками                                          |
| GET   | /team/get             | Получить команду с участникам                                          |
| POST  | /users/setIsActive    | Установить флаг активности пользователя                                |
| GET   | /users/getReview      | Получить PR'ы, где пользователь назначен ревьювером                    |
| POST  | /pullRequest/create   | Создать PR и автоматически назначить до 2 ревьюверов из команды автора |
| POST  | /pullRequest/merge    | Пометить PR как Merged                                                 |
| POST  | /pullRequest/reassign | Переназначить конкретного ревьювера на другого из его команды          |
---
## Примечания:

* Не успел сделать тестирование кодом, поэтому настроил коллекцию в PostMan;
* Добавил свой флаг BAD_REQUEST для enum ErrorCode, чтобы покрывать ошибки, которые не учтены в спецификации;
* Написал небольшой валидатор в utils (DataValidator), чтобы не дублировать длинные проверки в условных конструкциях -> DRY;
* Написал небольшой генератор ответа от сервера в generators (ResponseGenerator), чтобы не дублировать код внутри одного метода -> DRY;
* Хоть и присутствует в pom.xml зависимость для работы с JSONschem'ами, но не успел разобраться в библиотеке и сделать вменяемую валидацию входящих данных;
* Выбор в пользу InMemoryDataStorage сделан с целью упрощения реализации (чтобы не мучиться с SQL коннектами XD).
---

