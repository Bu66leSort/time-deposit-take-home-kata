# Time Deposit Refactoring Kata - Take-Home Assignment

## XA Bank Time Deposit

### Context
A junior developer implemented domain logic for a time deposit system but did not complete the API functionality. Your task is to refactor the existing codebase to implement all required functionalities based on the provided business requirements, ensuring no breaking changes occur.

### Requirements

1. **API Endpoints**:
    - Create a RESTful API endpoint to update the balances of all time deposits in the database.
    - Create a RESTful API endpoint to retrieve all time deposits.
        - The GET endpoint should return a list of all time deposits with the following schema:
            - `id`
            - `planType`
            - `balance`
            - `days`
            - `withdrawals`

2. **Database Setup**:
    - Store all time deposit plans in a database.
    - Define the following tables:
        - `timeDeposits`:
            - `id`: Integer (primary key)
            - `planType`: String (required)
            - `days`: Integer (required)
            - `balance`: Decimal (required)
        - `withdrawals`:
            - `id`: Integer (primary key)
            - `timeDepositId`: Integer (foreign key, required)
            - `amount`: Decimal (required)
            - `date`: Date (required)

3. **Interest Calculation**:
    - Implement logic to calculate monthly interest based on the plan type:
        - **Basic Plan**: 1% interest
        - **Student Plan**: 3% interest (no interest after 1 year)
        - **Premium Plan**: 5% interest (interest starts after 45 days)
    - No interest is applied for the first 30 days for any existing plans.

4. **Refactoring Constraints**:
    - Do not introduce breaking changes to the shared `TimeDeposit` class or modify the `updateBalance` method signature.
    - Ensure the design is extensible to accommodate future complexities in interest calculations.

5. **Code Quality**:
    - Adhere to SOLID principles, design patterns, and clean code practices where applicable.

6. **AI-Assisted Development**:
    - Set up an AI harness or agent workflow and use it throughout the development for this take-home exercise.
    - Briefly document the tools and setup used (e.g., LLMs, coding assistants, agentic frameworks, configuration).
    - Ensure your AI setup is practical and reproducible.
    - Include any custom rules, system prompts, or agent configurations used.
    - Include a brief summary of which parts of the solution were AI-assisted and why.

### Important Guidelines
- The existing `TimeDepositCalculator.updateBalance` method is functioning correctly. Ensure its behavior remains unchanged after refactoring.
- The final solution must include **exactly two API endpoints**. Do not develop additional endpoints.
- **Do not** create a pull request or a new branch in the ikigai-digital repository. Instead, fork the repository into your own GitHub repository and develop the solution there.
- Handling invalid input or exceptions is not required.
- Use any tools, frameworks, or libraries you find suitable.
- In case of ambiguity, make logical assumptions and justify them in code comments.

### Preferred Stack
- Use an OpenAPI Swagger contract.
- Embrace Hexagonal Architecture.
- Follow atomic commit practices.
- Utilize testcontainers.
- Leverage AI-assisted development tools for code generation, testing, and refactoring.

### Submission Instructions
- Provide clear instructions on how to trigger the endpoints using the Swagger contract.
- Email the link to your public GitHub repository.

---

# Solution (Java, `java/` module)

## How to run and trigger the endpoints

Requirements: Java 21, Maven, Docker.

```bash
cd java
docker compose up -d          # PostgreSQL on localhost:5432
mvn spring-boot:run           # Liquibase creates the schema and loads 6 demo deposits
```

Open Swagger UI: <http://localhost:8080/swagger-ui.html> (OpenAPI JSON: `/v3/api-docs`).

1. `GET /api/v1/time-deposits` -> "Try it out" -> "Execute": lists deposits with their withdrawals (`page=0`, `size=20`, max 100).
2. `PATCH /api/v1/time-deposits/balances` -> "Try it out" -> "Execute": accrues the monthly interest, returns `{"updatedDepositsCount": N}`.
3. Call `GET` again to see the new balances. A second `PATCH` in the same month returns `0`.

Tests (need Docker, PostgreSQL runs in Testcontainers): `cd java && mvn test`.

## Architecture (hexagonal)

```
org.ikigaidigital           TimeDeposit, TimeDepositCalculator (shared classes, original package kept)
├── domain                  Withdrawal, TimeDepositView
│   └── interest            InterestStrategy + basic / student / premium
├── application             TimeDepositService (use cases), AccrualChunkProcessor
│   └── port.in / port.out  UpdateBalancesUseCase, GetTimeDepositsUseCase / TimeDepositPort
├── adapter.in.rest         controller, response records, MapStruct mapper
├── adapter.out.persistence JPA entities, Spring Data repository, MapStruct mapper, port adapter
└── config                  Clock and calculator beans
```

`TimeDepositCalculator.updateBalance` keeps its signature and behaviour (covered by characterization tests written
before the refactoring). New plans are added by implementing `InterestStrategy` and passing it to the calculator.

## Assumptions

- **Balance is net of withdrawals.** Somebody else already deducted them; `updateBalance` does not read them, so
  `withdrawals` are returned by GET as history only. A withdrawal endpoint is out of scope (exactly two endpoints are required).
- **HTTP method.** `PATCH /api/v1/time-deposits/balances` (partial update of balances). It is idempotent per calendar month.
- **Interest is accrued once per calendar month per deposit** (`last_accrual_month`). Calling the endpoint repeatedly
  in a month does nothing. A deposit in its 30-day grace period is also marked for that month and starts earning in the next run.
- **Missed months are not caught up.** The endpoint is expected to be triggered every month (e.g. by a scheduler);
  if a month is skipped, the next run accrues interest for the current month only.
- **`days`** is stored (required by the task) and refreshed on every update, but the source of truth is `created_on`
  (`today - days` at creation); GET and the calculation derive `days` from it using the injected `Clock`.
- **`TimeDeposit` keeps `Double`** (shared class, no breaking changes). The database stores `numeric(19,2)`; conversion
  happens in the persistence mapper. `TimeDeposit` and `TimeDepositCalculator` stay in their original package
  `org.ikigaidigital`, so existing imports keep compiling.
- **Unknown plan types** earn no interest (as in the original code).
- **Update runs in chunks of 500** (keyset by id, one transaction per chunk, `PESSIMISTIC_WRITE`, JDBC batching).
  A failing chunk rolls back alone; re-running the endpoint continues with the not yet accrued deposits.
- **Tables and columns use snake_case** (`time_deposits`, `plan_type`, ...) following PostgreSQL conventions.
- **Demo data** is loaded by a Liquibase changeset with context `demo` (disabled in tests) because there is no create endpoint.
- Invalid input handling is not implemented (not required); `size` above 100 is silently capped.

## AI-assisted development

**Tools.** Claude Code (CLI) with Claude Sonnet 5.5, run in the repository. No other agent frameworks.

**Setup / rules.** A global `CLAUDE.md` with working rules was loaded in every session. The relevant ones:
- Discuss 2-3 options with trade-offs before coding on non-trivial tasks; flag architectural forks instead of deciding silently.
- Keep it minimal: no extra layers, caches or dependencies that were not asked for; do not refactor unrelated code.
- Java/Spring conventions: constructor injection, MapStruct for mapping, records for DTOs, `BigDecimal` for money,
  transactions in the service layer, Liquibase for schema changes, integration tests on Testcontainers with SQL fixtures.
- Report risks: race conditions, N+1, idempotency, money correctness.

**Workflow.**
1. Design first, in dialogue: the decisions (idempotency, chunking and locking, read model, PATCH, `Clock`) were discussed and
   chosen by me before any code was written; the assistant listed options and trade-offs.
2. Characterization tests for the calculator first, then the strategy refactoring against them.
3. Implementation in small commits (build, schema, application layer, persistence, REST, tests).

**What was AI-assisted.** Most of the code (strategies, entities, mappers, adapters, tests, Liquibase) was generated by the
assistant from the agreed design and reviewed by me; the architecture decisions and the assumptions above were mine.
Boilerplate and test scaffolding benefit most from AI; the interest-calculation behaviour was pinned by tests rather than trusted.
