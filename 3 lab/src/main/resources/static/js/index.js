let page = 0;
const filterForm = document.getElementById("filter-form");
filterForm.addEventListener("submit", (e) => {
  e.preventDefault();
  page = 0;
  loadStudents();
});
filterForm.addEventListener("reset", () =>
  setTimeout(() => {
    page = 0;
    loadStudents();
  }),
);
document.getElementById("prev-page").onclick = () => {
  page--;
  loadStudents();
};
document.getElementById("next-page").onclick = () => {
  page++;
  loadStudents();
};
document.querySelector(".add-button").onclick = () =>
  (location.href = "/add-student/");
async function loadStudents() {
  showPageError("");
  const params = new URLSearchParams();
  new FormData(filterForm).forEach((value, key) => {
    const clean = String(value).trim();
    const field = filterForm.elements.namedItem(key);
    if (clean) params.set(key, clean);
    // Whitespace-only search must not silently become an unrestricted list.
    else if (field?.dataset.whitespaceOnly === "true") params.set(key, "");
  });
  params.set("page", page);
  const response = await apiFetch("/api/user/students?" + params);
  const result = await response.json();
  if (!response.ok) {
    showPageError(result.error?.message || "Ошибка загрузки");
    return;
  }
  const table = document.getElementById("student-table");
  table.replaceChildren();
  for (const student of result.data) {
    const row = document.createElement("tr");
    for (const value of [student.name, student.group, student.ISU]) {
      const cell = document.createElement("td");
      cell.textContent = value;
      row.append(cell);
    }
    const actions = document.createElement("td");
    const container = document.createElement("div");
    container.className = "container-div";
    function button(text, action, cls) {
      const b = document.createElement("button");
      b.textContent = text;
      b.className = cls;
      b.onclick = action;
      container.append(b);
    }
    button(
      "Подробная информация",
      () => (location.href = "/student/?id=" + student.ISU),
      "info-button",
    );
    if (isAdmin()) {
      button(
        "Редактировать",
        () => (location.href = "/add-student/?id=" + student.ISU),
        "edit-button",
      );
      button(
        "Удалить",
        async () => {
          if (!confirm("Удалить студента " + student.name + "?")) return;
          const r = await apiFetch("/api/admin/students/" + student.ISU, {
            method: "DELETE",
          });
          if (!r.ok) {
            showPageError((await r.json()).error?.message);
            return;
          }
          if (result.data.length === 1 && page > 0) page--;
          await loadStudents();
        },
        "delete-button",
      );
    }
    actions.append(container);
    row.append(actions);
    table.append(row);
  }
  if (!result.data.length) {
    const row = document.createElement("tr"),
      cell = document.createElement("td");
    cell.colSpan = 4;
    cell.textContent = "Студенты не найдены";
    row.append(cell);
    table.append(row);
  }
  document.getElementById("page-info").textContent =
    `Страница ${page + 1} из ${Math.max(1, result.totalPages)} · Всего: ${result.totalElements}`;
  document.getElementById("prev-page").disabled = page === 0;
  document.getElementById("next-page").disabled = page + 1 >= result.totalPages;
}
loadStudents();
