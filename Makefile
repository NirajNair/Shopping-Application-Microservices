## Stops docker-compose (if running), builds all projects and starts docker compose
up_build: build_product build_order
	@echo Stopping docker images
	docker-compose down
	@echo Building and starting docker images...
	docker-compose up --build -d
	@echo Docker images built and started!

## Builds product-service executable
build_product:
	@echo Building product-service executable
	chdir product-service && mvn clean package
	@echo Built product-service executable

## Builds order-service executable
build_order:
	@echo Building order-service executable
	chdir order-service && mvn clean package
	@echo Built order-service executable
