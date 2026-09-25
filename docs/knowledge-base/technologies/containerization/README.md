# Containerization

Container runtime ve local multi-container orchestration teknolojileri.

## Day 1–7

- [Docker](docker.md)
- [Docker Compose](docker-compose.md)
- [Docker Compose Profiles](docker-compose-profiles.md)

## Kavramsal ayrım

- Docker -> container runtime/platform
- Docker Image -> immutable container template
- Docker Container -> runtime process instance
- Docker Compose -> multi-container declarative orchestration
- Docker Compose Profiles -> selective service activation

## Bu projedeki kullanım

Day 7 ile polyglot datastore infrastructure ve RabbitMQ Compose üzerinden yönetilir hale getirilmiş, heavy service'ler selective profile'lara ayrılmıştır.

## Sonraki roadmap kapsamı

Backend tamamlandıktan sonra containerization konusu:
- Docker image hardening
- multi-stage builds
- resource limits
- container security
- registry usage
- Kubernetes

ile genişletilecektir.
