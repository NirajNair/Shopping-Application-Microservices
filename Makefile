## Stops docker-compose (if running), builds all projects and starts docker compose
up_build: build_product build_order build_inventory build_cart
	@echo Stopping docker images
	docker-compose down
	@echo Building and starting docker images...
	docker-compose up --build -d
	@echo Docker images built and started!

up:
	@echo Stopping docker images
	docker-compose down
	@echo Compose up the docker images...
	docker-compose up --build -d
	@echo Docker containers started

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

## Builds inventory-service executable
build_inventory:
	@echo Building inventory-service executable
	chdir inventory-service && mvn clean package
	@echo Built inventory-service executable

## Builds cart-service executable
build_cart:
	@echo Building cart-service executable
	chdir cart-service && mvn clean package
	@echo Built cart-service executable