const params = new URLSearchParams(window.location.search);
const editId = Number(params.get("id"));
const hasEditId = params.has("id") && !Number.isNaN(editId);

let students = getStudentsFromCookie();

if (students.length > 0){
    console.log(students);
}else{
    console.log("Студентов нет");
}

const editStudent = students.find(function(student){
    return student.id === editId;
});

if (editStudent) {
    document.getElementById("full-name").value = editStudent.fullName;
    document.getElementById("group").value = editStudent.group;
    document.getElementById("ISU").value = editStudent.ISU;
    document.getElementById("number-house").value = editStudent.numberHouse;
    document.getElementById("number-room").value = editStudent.numberRoom;
    document.getElementById("date").value = editStudent.date;
    document.getElementById("inostran").checked = editStudent.inostran;
    document.getElementById("note").value = editStudent.note;
}

if (hasEditId && !editStudent) {
    console.log("Студент для редактирования не найден");
    alert("Студент для редактирования не найден");
    window.location.href = "index.html";
}


const form = document.getElementById("UserForm");

form.addEventListener("submit", function(event) {

    if (!validate()) {
        event.preventDefault();
        return;
    }

    event.preventDefault();

    const full_name = document.getElementById("full-name").value;
    const group = document.getElementById("group").value;
    const ISU = document.getElementById("ISU").value;
    const number_house = document.getElementById("number-house").value;
    const number_room = document.getElementById("number-room").value;
    const date = document.getElementById("date").value;
    const inostran = document.getElementById("inostran").checked;
    const note = document.getElementById("note").value;

    const user = {
        id: hasEditId ? editId : Date.now(),
        fullName: full_name,
        group: group,
        ISU: Number(ISU),
        numberHouse: Number(number_house),
        numberRoom: Number(number_room),
        date: date,
        inostran: inostran,
        note: note
    };


    if (hasEditId) {
        students = students.map(function(student) {
            if (student.id === editId) {
                return user;
            }

            return student;
        });
    } else {
        students.push(user);
    }

        console.log(students);
    
    const data = encodeURIComponent(JSON.stringify(students));
    document.cookie = `students=${data}; path=/; max-age=31536000`;

    window.location.href = "index.html";

});

function getStudentsFromCookie() {
    const match = document.cookie.match(/(?:^|;\s*)students=([^;]*)/);
    if (!match) return [];

    try {
        return JSON.parse(decodeURIComponent(match[1]));
    } catch (error) {
        // Если cookie повредили вручную, страница формы не должна падать при открытии.
        console.log(error);
        return [];
    }
}
