# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Project Overview

WebSphere Application Server / Java EE diagnostic tool — a WAR deployed to IBM Open Liberty (dev) or WAS (production). Provides servlet-based utilities for heap monitoring, thread dumping, JNDI testing, JMX inspection, and deliberate fault injection.

## Build & Run Commands

```bash
mvn package          # Build WAR
mvn liberty:dev      # Run on Open Liberty with hot reload
mvn liberty:run      # Build and start Liberty server
```

There are **no unit tests** in this project (`src/test/` does not exist).

## Key Architectural Constraints

- **Java 1.8 maximum** — `pom.xml` sets `maven.compiler.source/target=1.8`. Lambdas are fine; `streams`, `var`, records are not.
- **`javaee-api` is `provided`** — do not add compile-scope JAR dependencies; the app server supplies the runtime classpath.
- **`server.xml`** only enables `localConnector-1.0`, `jsp-2.3`, `jndi-1.0`. Adding features requires editing [`server.xml`](src/main/liberty/config/server.xml).
- **All URLs are auth-protected** by a `/*` security constraint in [`web.xml`](src/main/webapp/WEB-INF/web.xml) — role `authed`. No way to expose an unauthenticated endpoint without modifying `web.xml`.
- **Dual-runtime target**: developed on Open Liberty, deployed to traditional WAS in production. Avoid Liberty-only APIs.

## Servlet Registration Pattern

**No `@WebServlet` annotations** — servlet registration is exclusively via [`web.xml`](src/main/webapp/WEB-INF/web.xml). A servlet without both a `<servlet>` declaration and a `<servlet-mapping>` will 404. The `web.xml` uses the **Servlet 2.3 DTD** (not schema) — do not upgrade the DOCTYPE; it is intentional for broad WAS compatibility.

## Utility Classes

- [`com.ibm.jp.util.Html`](src/main/java/com/ibm/jp/util/Html.java) — **always** use `Html.escapeChar()` for dynamic content in HTML output. Use `Html.escapeStackTrace()` when printing exception stack traces. Do not inline-escape manually.
- [`com.ibm.jp.util.Env`](src/main/java/com/ibm/jp/util/Env.java) — `Env.isTiger()` checks if JVM >= 1.5 ("Tiger" was Sun's Java 5 codename). Used in JSPs for conditional feature display; not a mistake.
- [`com.ibm.jp.util.ClassCache`](src/main/java/com/ibm/jp/util/ClassCache.java) — `WeakReference`-based class cache. Use when caching `Class<?>` objects to avoid classloader leaks.

## Code Style

- **Tabs** for indentation (not spaces)
- Servlets primarily override `service()` directly (not `doGet()`/`doPost()`), though both patterns exist in the codebase
- HTML output written inline via `out.println()` — no templating engine; raw HTML strings in Java is the project norm
- `static final long serialVersionUID` required on all `HttpServlet` subclasses
- Some files are decompiled artifacts (note `// Decompiled by Jad` header in [`HeapWatchServlet.java`](src/main/java/com/ibm/jp/support/heap/HeapWatchServlet.java)) — preserve existing style
- Package structure: `com.ibm.jp.support.<feature>` for servlets, `com.ibm.jp.util` for shared utilities

## JSP Placement

- JSPs under `src/main/webapp/WEB-INF/` (e.g., `setcookie.jsp`, `LookupTest.jsp`) are **not directly accessible** — they must be reached via `RequestDispatcher.forward()` from a servlet.
- JSPs under `src/main/webapp/` directly (e.g., `index.jsp`, `senderr.jsp`) are directly accessible via URL.

## HeapWatchServlet Caveat

`HeapWatchServlet` uses `static` fields for watcher state — it is effectively a singleton per classloader. Do not instantiate it directly or copy its pattern for multi-instance use. WAS deployment descriptors [`ibm-web-bnd.xmi`](src/main/webapp/WEB-INF/ibm-web-bnd.xmi) and `ibm-web-ext.xmi` are WAS-specific (not standard Java EE); they control WAS binding/extension behavior.
