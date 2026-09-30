# Agent Coding Rules

## Non-Obvious Constraints

- **No `@WebServlet` annotations** — servlet registration is exclusively via [`web.xml`](../../src/main/webapp/WEB-INF/web.xml). A servlet without both a `<servlet>` + `<servlet-mapping>` entry will 404.
- **Use `Html.escapeChar()`** from [`com.ibm.jp.util.Html`](../../src/main/java/com/ibm/jp/util/Html.java) for any dynamic content in HTML output. Use `Html.escapeStackTrace()` for exception stack traces. Never inline-escape manually.
- **Java 8 maximum** — `pom.xml` sets `maven.compiler.source/target=1.8`. Lambdas are fine; streams/var/records are not.
- **`javaee-api` is `provided`** — do not add compile-scope JAR dependencies; everything runs on the app server's classpath.
- **New Liberty features** require editing [`server.xml`](../../src/main/liberty/config/server.xml) `<featureManager>` block; the current set is `localConnector-1.0`, `jsp-2.3`, `jndi-1.0`.
- **All URLs are auth-protected** by a `/*` security constraint in `web.xml` — no additional security code needed, but also no way to expose an unauthenticated endpoint without modifying `web.xml`.
- **`HeapWatchServlet`** uses `static` fields for watcher state, making it effectively a singleton per classloader. Do not instantiate it or copy its pattern for multi-instance use.
- **Do not upgrade the `web.xml` DOCTYPE** — it intentionally uses the Servlet 2.3 DTD for broad WAS version compatibility.
- **JSPs in `WEB-INF/`** (e.g., `setcookie.jsp`) are not directly URL-accessible — forward to them via `RequestDispatcher`; JSPs in `webapp/` root are directly accessible.
- **`ClassCache`** in `com.ibm.jp.util` uses `WeakReference` to cache `Class<?>` objects — use it to avoid classloader leaks when caching class references.
