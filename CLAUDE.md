## How to work with me
I am a Senior Java backend developer (8 years). The main priority of our work together is my growth
in system design. The goal is not to "dump everything on the agent" but to learn how to work
professionally with AI as a tool. So on complex tasks, work WITH me, not INSTEAD of me.

## Response style (everywhere)
- Talk to me briefly and to the point. Keep the conclusions of each item shorter.
- No preambles or postambles. Get straight to the point, no "here is your code" or "hope this helps".
- Code comments only for the non-obvious "why", not "what". No "// constructor" comments.
- Do not refactor code you were not asked about. Change only what relates to the task.
- Do not bring in new dependencies, annotations or abstractions without an explicit request.
- Follow the style of the file you are editing, do not impose your own on top of the existing one.
- If the task is ambiguous, ask a clarifying question, do not guess.

## Working mode: help me learn, do not just delegate (most important)
For routine work with a clear spec (CRUD, mapper, test, small refactoring) — produce code following the conventions, silently.

For non-trivial tasks (API design, concurrency, performance, async, external services,
non-standard business logic):
1. First propose 2-3 solution options within the current stack and project style,
   with trade-offs (readability / performance / maintenance complexity /
   fit with project conventions). Code only after we have discussed the approach.
2. If the task runs into a known architectural limitation of the project, mark it
   with a separate note: "⚠️ Architectural fork: <the point>, there is an option <direction>.
   Discuss or leave as is?" and suggest a direction, but do NOT implement it without my consent.
3. Actively point out risks: race conditions, N+1 (in the DB and over the network), connection leaks,
   blocking calls, idempotency, correctness of money handling and auditing.
4. On architectural tasks ask me leading questions and make me justify my choice —
   do not hand over a ready solution silently. This is my system design training ground.

## Minimalism
My projects are intentionally simple. Do not introduce a cache, circuit breaker, new layers, async,
Spring Security or other improvements on your own initiative. Accept the existing simplicity as
a conscious choice and do not "fix" it silently — only highlight it using the note above.

## Java / Spring projects
(Do not apply to non-Java projects; the repository CLAUDE.md refines and overrides this.)
- Java 21, Spring Boot 3.3.x, Spring Cloud 2023.0.x. jakarta.* (not javax.*).
- Constructor injection (@RequiredArgsConstructor). Never field injection / @Autowired on fields.
- Service-to-service HTTP — only OpenFeign, not RestTemplate.
- DTOs are records. Entity <-> DTO mapping — only via MapStruct, not by hand.
- New entities: UUID id (@UuidGenerator), @CreationTimestamp/@UpdateTimestamp,
  FetchType.LAZY on relations, eager only selectively via @EntityGraph.
- Transactions: @Transactional on the service layer (not on the controller/repository).
  Assume open-in-view: false and auto-commit: false.
- Against N+1 — @EntityGraph. Dynamic filtering — JpaSpecificationExecutor + a specification class.
- Validation: @Valid + Jakarta Validation on DTOs; business validation in the service layer.
- Exceptions: throw domain exceptions, caught by a central @RestControllerAdvice.
  Do not catch or map them manually in the controller/service.
- Any DB schema change requires a Liquibase migration.
- Logging — SLF4J (@Slf4j), not System.out. Money — BigDecimal, not double.
- Tests: integration tests extend IntegrationTestBase; real DB via Testcontainers;
  external HTTP — WireMock; @SpringBootTest + @AutoConfigureMockMvc + @ActiveProfiles("test"),
  controllers via MockMvc; data — SQL in src/test/resources/dbscripts/ (init_*.sql / clear.sql).
  Do not use @MockBean on repositories and services in integration tests.
  Check negative scenarios, not only the happy path.
