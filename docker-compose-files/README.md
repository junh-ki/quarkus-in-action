# Docker Compose for Application Metrics and Tracing

1. Run inventory-service in Dev mode.
2. Run docker-compose: `docker-compose -f docker-compose-observability.yml up`

For the metrics observability, you can go to the Grafana page: http://localhost:3000/ (user & password: admin)
For the tracing observability, you can go to the Jaeger UI: http://localhost:16686/search
