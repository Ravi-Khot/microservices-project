# Production Readiness Plan for `microservices-project`

This plan reviews the current Spring Boot microservices stack (Eureka, API Gateway, Feign, JWT, Docker, CI/CD) and proposes concrete, production-level improvements.

## 1) Current state observed

- Service discovery and routing are present via Eureka + Spring Cloud Gateway.
- Inter-service communication uses OpenFeign (employee -> department).
- JWT auth exists at the gateway with a custom `WebFilter`.
- Dockerfiles and a `docker-compose.yml` orchestrate local containers.
- A GitHub Actions pipeline builds JARs and Docker images.

## 2) Highest-priority production gaps (P0)

## P0.1 Secret management and credential hardening

**Observed:** plaintext DB passwords and JWT secret in versioned config (`root@123`, fixed JWT secret).

**Actions:**
1. Move all secrets to environment variables or secret managers (Vault, AWS Secrets Manager, GCP Secret Manager).
2. Use separate credentials per service (least privilege) rather than shared root DB credentials.
3. Rotate JWT signing keys; support key IDs (`kid`) for zero-downtime key rotation.
4. Remove credentials from `docker-compose.yml` and properties; use `.env` + CI secrets.

## P0.2 Authentication and authorization model

**Observed:** gateway security allows all requests at Spring Security layer (`anyExchange().permitAll()`) and relies on custom filter path matching.

**Actions:**
1. Enforce default-deny with explicit allow-list rules in `SecurityWebFilterChain`.
2. Replace path-substring RBAC checks (`/create`, `/update`, `/delete`) with route/method-based policies.
3. Hash passwords (`BCryptPasswordEncoder`) and never compare plaintext.
4. Prefer OAuth2/OIDC (Keycloak/Auth0/Cognito) for enterprise-grade identity, refresh tokens, revocation, and MFA.
5. Add structured error responses for auth failures (avoid generic runtime exceptions).

## P0.3 Transport and perimeter security

**Actions:**
1. Terminate TLS at ingress/load balancer and enforce HTTPS.
2. Add mTLS for service-to-service traffic where compliance requires it.
3. Configure secure headers (HSTS, X-Content-Type-Options, CSP where relevant).
4. Add rate-limiting at gateway (Redis-backed token bucket) and IP throttling / WAF integration.

## P0.4 Database and schema lifecycle

**Observed:** `spring.jpa.hibernate.ddl-auto=update` in runtime profiles.

**Actions:**
1. Replace with Flyway/Liquibase migrations; set `ddl-auto=validate` in non-local environments.
2. Split service databases clearly (currently compose + department config point to `employeedb` in Docker profile).
3. Add DB connection pool tuning (Hikari min/max, timeouts) and query timeout defaults.
4. Add backup/restore automation and migration rollback testing.

## 3) Reliability and resiliency (P1)

## P1.1 Timeouts, retries, and circuit breaking standards

**Observed:** Resilience4j circuit breaker appears only in employee service config.

**Actions:**
1. Define standard connect/read timeouts for Feign clients.
2. Add retry with backoff + jitter only for idempotent operations.
3. Add bulkheads and rate limits for dependency protection.
4. Publish resilience metrics and alert on open-circuit rates.

## P1.2 Service discovery and deployment strategy

**Actions:**
1. For Kubernetes, prefer native service discovery over Eureka (or document rationale if keeping Eureka).
2. Configure healthcheck-aware registration and deregistration.
3. Add graceful shutdown and readiness/liveness/startup probes.
4. Use rolling/canary deployments with automatic rollback criteria.

## 4) Observability and operations (P1)

**Actions:**
1. Add `spring-boot-starter-actuator` to all services and expose only required endpoints.
2. Add Micrometer + Prometheus metrics and Grafana dashboards.
3. Implement distributed tracing (OpenTelemetry + Jaeger/Tempo).
4. Standardize structured JSON logging with trace/span correlation IDs.
5. Add SLOs and alert rules (latency, error rate, saturation, auth failures).

## 5) API governance and compatibility (P2)

**Actions:**
1. Version APIs (`/v1/...`) and maintain backward compatibility policy.
2. Add OpenAPI docs and contract tests for gateway routes + service APIs.
3. Adopt consistent error envelope with code/category/correlationId.
4. Add idempotency keys for non-idempotent create endpoints exposed via gateway.

## 6) Docker and runtime hardening (P2)

**Actions:**
1. Use minimal runtime images (distroless or slim JRE), run as non-root.
2. Pin base image digests and automate CVE scanning (Trivy/Grype/Snyk).
3. Add container resource limits and JVM container-aware options.
4. Add healthchecks in images/compose for all services, not only MySQL.

## 7) CI/CD improvements (P2)

**Observed:** CI builds and pushes `latest` images only, and skips tests.

**Actions:**
1. Run tests in CI (unit + integration + smoke).
2. Build versioned and immutable tags (`sha`, semver) in addition to `latest`.
3. Add dependency + image vulnerability scanning gates.
4. Add SBOM generation and signing/attestation (SLSA provenance, cosign).
5. Use build cache and matrix/parallel jobs for faster feedback.
6. Add deployment pipeline stages (dev -> staging -> prod) with approvals.

## 8) Suggested phased roadmap

### Phase 1 (1-2 weeks)
- Externalize secrets.
- Lock down gateway authz and password hashing.
- Introduce Flyway and stop runtime schema mutation.
- Enable Actuator + basic Prometheus metrics.

### Phase 2 (2-4 weeks)
- Add tracing, standardized logs, alerting/SLOs.
- Harden CI with tests, scans, immutable tags.
- Implement resilient timeout/retry standards across Feign clients.

### Phase 3 (4-8 weeks)
- Move to centralized IdP (OAuth2/OIDC).
- Add progressive delivery and rollback automation.
- Optional: reevaluate Eureka if migrating to Kubernetes-native discovery.

## 9) Practical checklist

- [ ] No plaintext credentials in repo.
- [ ] Gateway default-deny authorization model.
- [ ] Passwords hashed and audited.
- [ ] DB migrations managed by Flyway/Liquibase.
- [ ] Health/readiness/liveness on every service.
- [ ] Metrics + tracing + structured logging in place.
- [ ] CI includes tests, scan gates, immutable artifacts.
- [ ] Deployment rollback playbook documented and tested.
