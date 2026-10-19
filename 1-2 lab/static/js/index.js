loadStudents();

const filterForm = document.getElementById("filter-form");

filterForm.addEventListener("submit", function (event) {
  event.preventDefault();
  loadStudents(getFilters());
});

filterForm.addEventListener("reset", function () {
  setTimeout(function () {
    loadStudents();
  });
});

async function loadStudents(filters = null) {
  const response = filters
    ? await queryStudents(filters)
    : await fetch("/api/requests");
  const result = await response.json();

  if (!response.ok) {
    alert(result.error?.message || "Ошибка загрузки студентов");
    return;
  }

  renderStudents(result.data);
}

function getFilters() {
  const formData = new FormData(filterForm);
  const filters = {};

  formData.forEach(function (value, key) {
    const trimmedValue = String(value).trim();

    if (trimmedValue !== "") {
      filters[key] = trimmedValue;
    }
  });

  return filters;
}

function queryStudents(filters) {
  return fetch("/api/requests", {
    method: "QUERY",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(filters),
  });
}

function renderStudents(students) {
  const table = document.getElementById("student-table");
  table.textContent = "";
  students.forEach(function (student) {
    const row = document.createElement("tr");
    const fullNameCell = document.createElement("td");
    const groupCell = document.createElement("td");
    const isuCell = document.createElement("td");
    const actionCell = document.createElement("td");
    const buttonContainer = document.createElement("div");
    const editButton = document.createElement("button");
    const deleteButton = document.createElement("button");
    const infoButton = document.createElement("button");

    fullNameCell.textContent = student.name;
    groupCell.textContent = student.group;
    isuCell.textContent = student.ISU;

    buttonContainer.className = "container-div";

    editButton.className = "edit-button";
    editButton.dataset.id = student.ISU;
    editButton.textContent = "Редактировать";

    deleteButton.className = "delete-button";
    deleteButton.dataset.id = student.ISU;
    deleteButton.textContent = "Удалить";

    infoButton.className = "info-button";
    infoButton.dataset.id = student.ISU;
    infoButton.textContent = "Подробная информация";

    buttonContainer.append(editButton, deleteButton, infoButton);
    actionCell.append(buttonContainer);
    row.append(fullNameCell, groupCell, isuCell, actionCell);
    table.append(row);
  });

  bindButtons();
}

function bindButtons() {
  const deleteButtons = document.querySelectorAll(".delete-button");

  deleteButtons.forEach(function (button) {
    button.addEventListener("click", function () {
      const id = Number(button.dataset.id);

      deleteStudent(id);
    });
  });

  const editButtons = document.querySelectorAll(".edit-button");

  editButtons.forEach(function (button) {
    button.addEventListener("click", function () {
      const id = Number(button.dataset.id);
      window.location.href = `/add-student/?id=${id}`;
    });
  });

  const infoButtons = document.querySelectorAll(".info-button");

  infoButtons.forEach(function (button) {
    button.addEventListener("click", function () {
      const id = Number(button.dataset.id);
      window.location.href = `/student/?id=${id}`;
    });
  });
}

async function deleteStudent(ISU) {
  const response = await fetch(`/api/requests/${ISU}`, {
    method: "DELETE",
  });

  if (!response.ok) {
    const result = await response.json();
    alert(result.error?.message || "Ошибка удаления студента");
    return;
  }

  loadStudents();
}

const addButton = document.querySelector(".add-button");

addButton.addEventListener("click", function () {
  window.location.href = addButton.dataset.url;
});
