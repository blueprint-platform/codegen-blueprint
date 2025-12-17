<#-- README Generator Template -->

# ${projectName}

${projectDescription}

---

## ✨ Project Summary

| Domain | Aspect | Value |
|------|--------|-------|
| 🔧 Tech Stack | Framework | `${framework}` |
| | Language | `${language}` |
| | Build Tool | `${buildTool}` |
| | Java | `${javaVersion}` |
| | Spring Boot | `${springBootVersion}` |
| 🏗 Architecture | Layout | `${layout}` |
| | Enforcement | `${enforcement}` |
| | Sample Code | `${sampleCode}` |
---

## 📦 Coordinates

| Key          | Value            |
| ------------ | ---------------- |
| `groupId`    | `${groupId}`     |
| `artifactId` | `${artifactId}`  |
| `package`    | `${packageName}` |

---

## 🚀 Quick Start

```bash
./mvnw clean package     # Build (using wrapper)
./mvnw spring-boot:run   # Run the application
```

> If Maven is installed globally, you may also use `mvn` instead of `./mvnw`.

<#-- Auto-config hints based on selected features -->
<#if features?has_content && ((features.h2)!false || (features.actuator)!false || (features.security)!false)>

---

## ⚙️ Auto Configuration Notes

<#if (features.h2)!false>

### H2 (for JPA)

This project includes an **in-memory H2 database** configuration because `spring-boot-starter-data-jpa` was selected.

* JDBC URL: `jdbc:h2:mem:${artifactId}`
* Console: `/h2-console` (if enabled)

</#if>

<#if (features.actuator)!false>

### Actuator

Basic actuator exposure is enabled:

* `/actuator/health`
* `/actuator/info`

</#if>

<#if (features.security)!false>

### Security

`spring-boot-starter-security` is included. Endpoints may require authentication depending on defaults and your configuration.

</#if>

</#if>
---

## 📁 Project Layout

<#-- Hexagonal layout -->
<#if layout == "hexagonal">

```text
src
├─ main
│  ├─ java/${packageName?replace('.', '/')}
│  │  ├─ adapter
│  │  │  ├─ in
│  │  │  └─ out
│  │  ├─ application
│  │  │  ├─ port
│  │  │  └─ service
│  │  ├─ domain
│  │  │  ├─ model
│  │  │  └─ policy
│  │  └─ bootstrap
│  └─ resources
└─ test
└─ java/${packageName?replace('.', '/')}
```

> This project follows **Hexagonal Architecture (Ports & Adapters)**.
>
> * `domain` contains pure business rules (framework-free)
> * `application` orchestrates use cases and defines ports
> * `adapter` contains inbound/outbound implementations (REST, persistence, messaging)
> * `bootstrap` wires everything together

<#-- Standard / layered layout -->
<#else>

```text
src
├─ main
│  ├─ java/${packageName?replace('.', '/')}
│  │  ├─ controller
│  │  ├─ service
│  │  ├─ repository
│  │  ├─ domain
│  │  │  └─ model
│  │  └─ config
│  └─ resources
└─ test
└─ java/${packageName?replace('.', '/')}
```

> This project follows a **Standard Layered Architecture**.
>
> * `controller` handles HTTP/API requests
> * `service` contains business logic
> * `repository` manages persistence
> * `domain` holds core domain models
> * `config` contains application and framework configuration

</#if>

---

<#-- Hexagonal showcase (only for hexagonal layout) -->
<#if layout == "hexagonal">

---

## 🧱 Hexagonal Architecture (Ports & Adapters)

This project was generated with the **hexagonal** layout:

```bash
--layout hexagonal
```

Your `src/main/java/${packageName?replace('.', '/')}` structure follows these top-level modules:

* `domain` – core business rules (no Spring dependencies)
* `application` – use cases + port contracts
* `adapter` – inbound/outbound integrations (REST, DB, messaging, etc.)
* `bootstrap` – wiring/configuration (Spring Boot entrypoints)

### What this means in practice

* Dependencies point **inward** (adapters depend on application/domain; domain depends on nothing).
* The application layer exposes **ports**; adapters implement or call those ports.

</#if>

<#if layout == "hexagonal" && sampleCode == "basic">

---

## 🧪 Included Sample (Basic)

Because `--sample-code basic` was selected, the project includes a minimal end-to-end **Greeting** slice that demonstrates:

* an inbound **REST adapter**
* an application **use case** via an input port
* domain/application models
* mapping of use case output to HTTP response DTO

### Sample REST endpoints

Base path:

```text
/api/v1/sample/greetings
```

Available endpoints:

* `GET /api/v1/sample/greetings/default`
* returns a default greeting

* `GET /api/v1/sample/greetings?name=John`
* returns a personalized greeting

Example calls:

```bash
curl -s http://localhost:8080/api/v1/sample/greetings/default | jq
curl -s "http://localhost:8080/api/v1/sample/greetings?name=John" | jq
```

### Where to look in the code

* **Inbound REST adapter**

* `adapter/sample/in/rest/GreetingController`
* **Application port (input)**

* `application/sample/port/in/GetGreetingPort`
* **Use case implementation**

* `application/sample/usecase/...` (varies by generator version)

You can use this sample in two ways:

* as a **teaching reference** for the hexagonal boundaries in this codebase
* as a **starting slice** to evolve into your real business modules

</#if>

---

## 📚 Selected Dependencies

<#if dependencies?has_content>

| Dependency                                                                                    | Scope                                                                |
| --------------------------------------------------------------------------------------------- | -------------------------------------------------------------------- |
| <#list dependencies as d>                                                                     |                                                                      |
| `${d.groupId}:${d.artifactId}`<#if d.version?? && d.version?has_content>:`${d.version}`</#if> | <#if d.scope?? && d.scope?has_content>${d.scope}<#else>default</#if> |
| </#list>                                                                                      |                                                                      |
<#else>
> No additional dependencies were selected.>
</#if>

---

## 🧩 Next Steps

* ✔ Structure your domain and use case logic
* ✔ Add CI/CD pipelines or Docker support
* ✔ Configure profiles in `application.yml`
* ✔ Add more Spring Boot starters if needed

---

🏗 Generated by **Blueprint Platform — Codegen Blueprint CLI**
