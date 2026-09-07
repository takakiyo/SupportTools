# Plan Mode Architecture Rules

## Non-Obvious Architectural Constraints

- **Dual-runtime target**: developed/tested on Open Liberty, deployed to WebSphere Application Server. Plans must account for WAS compatibility; avoid Liberty-only APIs.
- **Minified Liberty packaging** (`<include>minify</include>`) — only declared `server.xml` features are bundled. Any new capability requiring a new Liberty feature needs a `server.xml` change AND must be verified available on the WAS target.
- **No build-time code generation** — no annotation processors, no Lombok, no CDI. All wiring is explicit (web.xml, manual instantiation).
- **`HeapWatchServlet` state is static** — the watcher thread and storage list are class-level statics. Plans involving multiple instances, clustering, or classloader isolation must account for this.
- **Context root `/support`** is hardcoded in `server.xml` and referenced in `web.xml` links — changing it requires coordinated updates in both files.
- **Security model is all-or-nothing**: one `<security-constraint>` covers `/*`. There is no role-based access differentiation between tools — all authenticated users see everything.
