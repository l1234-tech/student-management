const params = new URLSearchParams(window.location.search);
const id = Number(params.get("id"));

const table = document.getElementById("student-info-table");
table.textContent = "";

if (!params.has("id") || Number.isNaN(id)) {
  showNotFound("Некорректный ИСУ студента");
} else {
  loadStudent(id);
}

async function loadStudent(ISU) {
  const response = await fetch(`/api/requests/${ISU}`);
  const result = await response.json();

  if (!response.ok) {
    showNotFound(result.error?.message || "Студент не найден");
    return;
  }

  renderStudent(result.data);
}

function renderStudent(student) {
  table.textContent = "";

  addInfoRow("ФИО", student.name);
  addInfoRow("Группа", student.group);
  addInfoRow("ИСУ", student.ISU);
  addInfoRow("Общежитие", student.dormitory);
  addInfoRow("Комната", student.room);
  addInfoRow("Дата заселения", student.checkin_date);
  addInfoRow("Иностранец", student.is_foreign ? "Да" : "Нет");
  addInfoRow("Заметки", student.note || "");
}

function showNotFound(message) {
  table.textContent = "";

  const row = document.createElement("tr");
  const messageCell = document.createElement("td");

  messageCell.colSpan = 2;
  messageCell.textContent = message;
  row.append(messageCell);
  table.append(row);
}

function addInfoRow(type, value) {
  const row = document.createElement("tr");
  const typeCell = document.createElement("td");
  const valueCell = document.createElement("td");

  typeCell.textContent = type;
  valueCell.textContent = value;
  row.append(typeCell, valueCell);
  table.append(row);
}
