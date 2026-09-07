let students = [];

if (document.cookie.includes("students=")){
    const cookiesData = document.cookie.split("students=")[1];
    const decodedData = decodeURIComponent(cookiesData);
    students = JSON.parse(decodedData);
    console.log(students);
}else{
    console.log("Студентов нет");
}



 const table = document.getElementById("student-table");
 table.innerHTML = "";
 students.forEach(function(student){
    table.innerHTML += `<tr>
        <td>${student.fullName}</td>
        <td>${student.group}</td>
        <td>${student.ISU}</td>
        <td>
            <div class="container-div">  
                <button class="edit-button data-id="${student.id}">Редактировать</button>
                <button class="delete-button" data-id="${student.id}">Удалить</button>
                <button class="container-button" data-id="${student.id}">Подробная информация</button>
            </div>
        </td>
            
    </tr>`;  
})

