# Architecture Documentation

This directory contains system-level architecture documentation. Service-specific implementation design belongs inside each service's own `docs/DESIGN.md`.

## Documents

- `service-catalog.md` — service responsibility and datastore ownership
- `data-architecture.md` — database-per-service and datastore roles
- `communication-architecture.md` — REST, OpenFeign, gRPC, RabbitMQ and Kafka responsibilities
- `../adr/` — architecture decisions and trade-offs

## Documentation rule

Use the root `ROADMAP.md` for overall sequence, `docs/roadmap/day-XX-*.md` for milestone scope, service `ROADMAP.md` for service evolution, and service `docs/DESIGN.md` for technical design.
