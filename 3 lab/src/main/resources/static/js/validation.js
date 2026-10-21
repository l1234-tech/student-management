function validate() {
  const nameField = document.getElementById("full-name");
  const full_name = nameField.value.trim().replace(/ +/g, " ");
  nameField.value = full_name;
  const group = document.getElementById("group").value.trim();
  const ISU = document.getElementById("ISU").value.trim();
  const numberHouse = document.getElementById("number-house").value.trim();
  const numberRoom = document.getElementById("number-room").value.trim();
  const date = document.getElementById("date").value.trim();
  clearErrors();

  if (
    !/^[A-Za-zА-Яа-яЁё]{2,}(?:-[A-Za-zА-Яа-яЁё]{2,})*(?: [A-Za-zА-Яа-яЁё]{2,}(?:-[A-Za-zА-Яа-яЁё]{2,})*){1,2}$/.test(
      full_name,
    )
  ) {
    showError(
      "full-name",
      "Введите фамилию и имя, при необходимости отчество; используйте буквы, пробелы и дефисы",
    );
    return false;
  }

  if (full_name.length > 60) {
    showError("full-name", "ФИО должно быть не длиннее 60 символов");
    return false;
  }

  if (!/^[a-zA-Zа-яА-ЯёЁ][0-9]{4,5}$/.test(group)) {
    showError("group", "Группа должна начинаться с буквы, затем 4-5 цифр");
    return false;
  }

  if (!/^[1-9][0-9]{5}$/.test(ISU)) {
    showError("ISU", "ИСУ должен состоять из 6 цифр и не начинаться с нуля");
    return false;
  }

  const houseNumber = Number(numberHouse);
  if (!Number.isInteger(houseNumber) || houseNumber < 1 || houseNumber > 20) {
    showError("number-house", "Номер общежития должен быть от 1 до 20");
    return false;
  }

  const roomNumber = Number(numberRoom);
  if (!Number.isInteger(roomNumber) || roomNumber < 1000 || roomNumber > 9999) {
    showError("number-room", "Номер комнаты должен быть четырехзначным числом");
    return false;
  }

  const settlementDate = new Date(date);
  if (!date || Number.isNaN(settlementDate.getTime())) {
    showError("date", "Введите корректную дату заселения");
    return false;
  }

  if (settlementDate.getFullYear() < 2016) {
    showError(
      "date",
      "Вы не могли заселиться в такую дату, или вы уже не студент",
    );
    return false;
  }

  let today = new Date().toISOString().split("T")[0];
  if (date > today) {
    showError("date", "Дата заселения не может быть в будущем");
    return false;
  }

  return true;
}

function showError(fieldId, message) {
  const field = document.getElementById(fieldId);
  if (!field) return;

  field.classList.add("input-error");

  let errorContainer = field.parentElement.querySelector(".field-error");
  if (!errorContainer) {
    errorContainer = document.createElement("span");
    errorContainer.className = "field-error";
    field.parentElement.appendChild(errorContainer);
  }
  errorContainer.textContent = message;
}

function clearErrors() {
  document.querySelectorAll(".input-error").forEach((el) => {
    el.classList.remove("input-error");
  });
  document.querySelectorAll(".field-error").forEach((el) => {
    el.remove();
  });
}
