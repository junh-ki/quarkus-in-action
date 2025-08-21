# Docker Compose for Application Metrics and Tracing

1. Run inventory-service in Dev mode.
2. Run docker-compose: `docker-compose -f docker-compose-observability.yml up`

For the metrics observability, you can go to the Grafana page: http://localhost:3000/ (user & password: admin)
For the tracing observability, you can go to the Jaeger UI: http://localhost:16686/search

# Docker Compose for Infra (Not dependent on the Dev Services containers)

1. Run docker-compose: `docker-compose -f docker-compose-infra.yml up`
2. Verify all 12 containers are running: `docker ps --format "table {{.Names | printf \"%-20s\"}} {{.Image}}"`
   - keycloak
   - kafka
   - zookeeper
   - postgres-keycloak
   - mongodb-rental
   - rabbitmq
   - postgres-reservation
   - jaeger
   - mongodb-billing
   - prometheus
   - mysql
   - grafana
