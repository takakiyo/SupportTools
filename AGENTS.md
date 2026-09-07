# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Project Overview

WebSphere Application Server / Java EE diagnostic tool — a WAR deployed to IBM Open Liberty (dev) or WAS (production). Provides servlet-based utilities for heap monitoring, thread dumping, JNDI testing, JMX inspection, and deliberate fault injection.

## Build & Run Commands

```bash
# Build WAR
mvn package

# Build and run on Open Liberty (dev mode with hot reload)
mvn liberty:dev

# Build and start Liberty server
mvn liberty:run

# Package only (skip tests — there are none)
mvn package -DskipTests
```

There are **no unit tests** in this project (`src/test/` does not exist).

## Deployment Details

- Context root: `/support` (configured in [`server.xml`](src/main/liberty/config/server.xml))
- Default port: `9080` (HTTP), `9443` (HTTPS)
- WAR file name: `SupportTools.war` (set by `pom.xml` `<finalName>`)
- Liberty packaging uses `<include>minify</include>` — only required features are bundled

## Key Architectural Constraints

- Target Java source/target: **Java 1.8** (`maven.compiler.source/target = 1.8`). Do not use language features beyond Java 8.
- Dependency scope is `provided` (javaee-api 7.0) — runtime is supplied by the app server. Do not add runtime JAR dependencies.
- `server.xml` only enables `localConnector-1.0`, `jsp-2.3`, and `jndi-1.0` features. Adding new server features requires editing [`server.xml`](src/main/liberty/config/server.xml).
- All URLs require authentication (`<security-constraint>` covers `/*` with role `authed`). New servlets are immediately protected.

## Servlet Registration Pattern

New servlets must be registered **manually** in [`web.xml`](src/main/webapp/WEB-INF/web.xml) — no annotations (`@WebServlet`) are used. Each servlet needs both a `<servlet>` declaration and a `<servlet-mapping>`.

## Utility Classes

- [`com.ibm.jp.util.Html`](src/main/java/com/ibm/jp/util/Html.java) — use `Html.escapeChar()` for all user-supplied or dynamic content written to HTML responses. Do not use `String.replace()` or inline escaping.
- [`com.ibm.jp.util.Env`](src/main/java/com/ibm/jp/util/Env.java) — `Env.isTiger()` checks if JVM >= 1.5. Referenced in JSPs for conditional feature display.

## Code Style

- Tabs for indentation (not spaces)
- Servlets override `service()` directly rather than `doGet()`/`doPost()` in most cases
- HTML output written inline via `out.println()` — no templating engine; raw HTML strings in Java code is the project norm
- `static final long serialVersionUID` required on all `HttpServlet` subclasses
- Some files are decompiled artifacts (note `// Decompiled by Jad` header in [`HeapWatchServlet.java`](src/main/java/com/ibm/jp/support/heap/HeapWatchServlet.java)) — preserve existing style
- Package structure: `com.ibm.jp.support.<feature>` for servlets, `com.ibm.jp.util` for shared utilities
