// Один помощник для защищённых запросов и автоматического обновления токенов.
function saveTokens(tokens) {
  sessionStorage.setItem("tokens", JSON.stringify(tokens));
}
function getTokens() {
  try {
    return JSON.parse(sessionStorage.getItem("tokens") || "null");
  } catch {
    return null;
  }
}
function claims() {
  try {
    const raw = getTokens()
      .accessToken.split(".")[1]
      .replace(/-/g, "+")
      .replace(/_/g, "/");
    return JSON.parse(
      new TextDecoder().decode(
        Uint8Array.from(atob(raw), (c) => c.charCodeAt(0)),
      ),
    );
  } catch {
    return {};
  }
}
function isAdmin() {
  return claims().role === "ADMIN";
}
function showPageError(message) {
  document.getElementById("page-error").textContent = message;
}
let refreshPending;
async function apiFetch(url, options = {}) {
  let tokens = getTokens();
  if (!tokens) {
    location.href = "/login/";
    throw new Error("Войдите в систему");
  }
  const request = () =>
    fetch(url, {
      ...options,
      headers: {
        ...options.headers,
        Authorization: "Bearer " + getTokens().accessToken,
      },
    });
  let response = await request();
  if (response.status === 401) {
    if (!refreshPending)
      refreshPending = (async () => {
        const r = await fetch("/api/auth/refresh", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ refreshToken: getTokens().refreshToken }),
        });
        if (!r.ok) throw new Error("Сессия истекла. Войдите снова");
        saveTokens(await r.json());
      })().finally(() => {
        refreshPending = null;
      });
    try {
      await refreshPending;
      response = await request();
    } catch (e) {
      sessionStorage.removeItem("tokens");
      location.href = "/login/";
      throw e;
    }
  }
  return response;
}
async function signOut() {
  const tokens = getTokens();
  if (tokens)
    await fetch("/api/auth/logout", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refreshToken: tokens.refreshToken }),
    }).catch(() => {});
  sessionStorage.clear();
  location.href = "/login/";
}
if (!location.pathname.startsWith("/login")) {
  if (!getTokens()) location.replace("/login/");
  const user = claims();
  document.getElementById("account-name").textContent =
    (user.sub || "") + " · " + (user.role || "");
  document
    .querySelectorAll("[data-admin]")
    .forEach((el) => (el.hidden = !isAdmin()));
  document.getElementById("logout-button").onclick = signOut;
}
window.addEventListener("unhandledrejection", (e) => {
  showPageError(e.reason?.message || "Не удалось выполнить запрос");
});
