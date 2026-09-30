const params = new URLSearchParams(window.location.search);
const id = Number(params.get("id"));

console.log(id);

let students = [];

students = getStudentsFromCookie();

console.log(students);

const student = students.find(function(student){
    return student.id == id;
})

const table = document.getElementById("student-info-table");
table.textContent = "";

if (!student) {
    const row = document.createElement("tr");
    const messageCell = document.createElement("td");

    messageCell.colSpan = 2;
    messageCell.textContent = "Студент не найден";
    row.append(messageCell);
    table.append(row);
} else {
    addInfoRow("ФИО", student.fullName);
    addInfoRow("Группа", student.group);
    addInfoRow("ИСУ", student.ISU);
    addInfoRow("Общежитие", student.numberHouse);
    addInfoRow("Комната", student.numberRoom);
    addInfoRow("Дата заселения", student.date);
    addInfoRow("Иностранец", student.inostran ? "Да" : "Нет");
    addInfoRow("Заметки", student.note);
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

function getStudentsFromCookie() {
    const match = document.cookie.match(/(?:^|;\s*)students=([^;]*)/);
    if (!match) return [];

    try {
        return JSON.parse(decodeURIComponent(match[1]));
    } catch (error) {
        // Если cookie повредили вручную, страница подробностей должна показать пустой результат.
        console.log(error);
        return [];
    }
}
