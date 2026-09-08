const params = new URLSearchParams(window.location.search);
const id = Number(params.get("id"));

console.log(id);

let students = [];

if (document.cookie.includes("students=")){
    const cookiesData = document.cookie.split("students=")[1];
    const decodedData = decodeURIComponent(cookiesData);
    students = JSON.parse(decodedData);
}

console.log(students);

const student = students.find(function(student){
    return student.id == id;
})

const table = document.getElementById("student-info-table");
table.innerHTML = ""
table.innerHTML = `
    <tr>
        <td>ФИО</td>
        <td>${student.fullName}</td>
    </tr>
    <tr>
        <td>Группа</td>
        <td>${student.group}</td>
    </tr>
    <tr>
        <td>ИСУ</td>
        <td>${student.ISU}</td>
    </tr>
    <tr>
        <td>Общежитие</td>
        <td>${student.numberHouse}</td>
    </tr>
    <tr>
        <td>Комната</td>
        <td>${student.numberRoom}</td>
    </tr>
    <tr>
        <td>Дата заселения</td>
        <td>${student.date}</td>
    </tr>
    <tr>
        <td>Иностранец</td>
        <td>${student.inostran ? "Да" : "Нет"}</td>
    </tr>
    <tr>
        <td>Заметки</td>
        <td>${student.note}</td>
    </tr>

`