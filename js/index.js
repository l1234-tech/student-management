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
                <button class="container-button edit-button" data-id="${student.id}">Редактировать</button>
                <button class="container-button delete-button" data-id="${student.id}">Удалить</button>
                <button class="container-button info-button" data-id="${student.id}">Подробная информация</button>
            </div>
        </td>
            
    </tr>`;  
})

const deleteButtons = document.querySelectorAll(".delete-button");

deleteButtons.forEach(function(button){
    button.addEventListener("click",function(){
        const id =Number(button.dataset.id);

        students = students.filter(function(student){
            return student.id !==id;
        })

        const data = encodeURIComponent(JSON.stringify(students));
        document.cookie = `students=${data}`;
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
    window.location.href = "student_form.html";
});
