# Ask Mode Context

## Non-Obvious Documentation Context

- The target runtime is **WebSphere Application Server** (WAS) in production, but development uses **IBM Open Liberty** via `liberty-maven-plugin`. These are compatible but not identical — Liberty-specific features may not exist on WAS.
- `src/main/webapp/WEB-INF/ibm-web-bnd.xmi` and `ibm-web-ext.xmi` are **WAS-specific deployment descriptors** (not standard Java EE). They control WAS-specific binding/extension behavior.
- `Env.isTiger()` is a legacy check for Java >= 1.5 ("Tiger" was Sun's codename). It is still in JSPs for historical compatibility — not a mistake.
- `HeapWatchServlet.java` has a `// Decompiled by Jad` header — it was originally a compiled-only artifact that was decompiled and checked in as source. Treat existing logic with caution.
- There are **no tests** and no test infrastructure at all in this project.
- The `web.xml` uses the **Servlet 2.3 DTD** (not schema) — this is intentional for broad WAS version compatibility; do not suggest upgrading it.
- `Html` utility has three public methods: `escapeChar()` (HTML entities), `escapeStackTrace()` (for exceptions), `unescapeChar()` (reverse), and `decodeUTF8()` (decodes `&#NNNN;` numeric references sent by old MSIE).
- `ClassCache` stores `Class<?>` objects via `WeakReference` to prevent classloader leaks — relevant context for heap/classloader troubleshooting questions.
- The `index.jsp` welcome page uses `Env.isTiger()` to conditionally show the Thread Dump link — features gated by `Env.isTiger()` require Java 5+.
