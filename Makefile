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
