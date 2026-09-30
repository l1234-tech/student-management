let students = [];

students = getStudentsFromCookie();

if (students.length > 0){
    console.log(students);
}else{
    console.log("Студентов нет");
}


 const table = document.getElementById("student-table");
 table.textContent = "";
  students.forEach(function(student){
    const row = document.createElement("tr");
    const fullNameCell = document.createElement("td");
    const groupCell = document.createElement("td");
    const isuCell = document.createElement("td");
    const actionCell = document.createElement("td");
    const buttonContainer = document.createElement("div");
    const editButton = document.createElement("button");
    const deleteButton = document.createElement("button");
    const infoButton = document.createElement("button");

    fullNameCell.textContent = student.fullName;
    groupCell.textContent = student.group;
    isuCell.textContent = student.ISU;

    buttonContainer.className = "container-div";

    editButton.className = "edit-button";
    editButton.dataset.id = student.id;
    editButton.textContent = "Редактировать";

    deleteButton.className = "delete-button";
    deleteButton.dataset.id = student.id;
    deleteButton.textContent = "Удалить";

    infoButton.className = "info-button";
    infoButton.dataset.id = student.id;
    infoButton.textContent = "Подробная информация";

    buttonContainer.append(editButton, deleteButton, infoButton);
    actionCell.append(buttonContainer);
    row.append(fullNameCell, groupCell, isuCell, actionCell);
    table.append(row);
})

const deleteButtons = document.querySelectorAll(".delete-button");

deleteButtons.forEach(function(button){
    button.addEventListener("click",function(){
        const id =Number(button.dataset.id);

        students = students.filter(function(student){
            return student.id !==id;
        })

        const data = encodeURIComponent(JSON.stringify(students));
        document.cookie = `students=${data}; path=/; max-age=31536000`;
        window.location.reload();
    });

});

const editButtons = document.querySelectorAll(".edit-button");

editButtons.forEach(function(button){
    button.addEventListener("click",function(){
        const id = Number(button.dataset.id);
        window.location.href = `student_form.html?id=${id}`;
    });
});

const infoButtons = document.querySelectorAll(".info-button");

infoButtons.forEach(function(button){
    button.addEventListener("click",function(){
        const id = Number(button.dataset.id);
        window.location.href = `student.html?id=${id}`;
    });
});

const addButton = document.querySelector(".add-button");

addButton.addEventListener("click",function(){
    window.location.href = addButton.dataset.url;
});

function getStudentsFromCookie() {
    const match = document.cookie.match(/(?:^|;\s*)students=([^;]*)/);
    if (!match) return [];

    try {
        return JSON.parse(decodeURIComponent(match[1]));
    } catch (error) {
        // Если cookie повредили вручную, главная страница должна открываться без падения.
        console.log(error);
        return [];
    }
}
