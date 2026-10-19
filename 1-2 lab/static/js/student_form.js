const params = new URLSearchParams(window.location.search);
const editId = Number(params.get("id"));
const hasEditId = params.has("id") && !Number.isNaN(editId);

if (hasEditId) {
  loadStudent(editId);
}

const form = document.getElementById("UserForm");

form.addEventListener("submit", async function (event) {
  event.preventDefault();

  if (!validate()) {
    return;
  }

  const student = {
    name: document.getElementById("full-name").value.trim(),
    group: document.getElementById("group").value.trim(),
    ISU: Number(document.getElementById("ISU").value),
    dormitory: Number(document.getElementById("number-house").value),
    room: Number(document.getElementById("number-room").value),
    checkin_date: document.getElementById("date").value,
    is_foreign: document.getElementById("inostran").checked,
    note: document.getElementById("note").value,
  };

  const url = hasEditId ? `/api/requests/${editId}` : "/api/requests";
  const method = hasEditId ? "PATCH" : "POST";

  const response = await fetch(url, {
    method: method,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(student),
  });

  if (!response.ok) {
    const result = await response.json();
    alert(result.error?.message || "Ошибка сохранения студента");
    return;
  }

  window.location.href = "/";
});

async function loadStudent(ISU) {
  const response = await fetch(`/api/requests/${ISU}`);
  const result = await response.json();

  if (!response.ok) {
    alert(result.error?.message || "Студент для редактирования не найден");
    window.location.href = "/";
    return;
  }

  fillForm(result.data);
}

function fillForm(student) {
  document.getElementById("full-name").value = student.name;
  document.getElementById("group").value = student.group;
  document.getElementById("ISU").value = student.ISU;
  document.getElementById("number-house").value = student.dormitory;
  document.getElementById("number-room").value = student.room;
  document.getElementById("date").value = student.checkin_date;
  document.getElementById("inostran").checked = student.is_foreign;
  document.getElementById("note").value = student.note || "";
}
