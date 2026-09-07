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
        <td>${student.numberHouse}</td>
        <td>${student.numberRoom}</td>
        <td>
            <div class="container-div>
                <button class="container-button">Редактировать</button>
                <button class="container-button">Удалить</button>
                <button class="container-button">Подробная информация</button>
            </div>
        </td>
            
    </tr>`;  
})
