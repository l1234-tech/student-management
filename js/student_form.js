const form = document.getElementById("UserForm");

form.addEventListener("submit", function(event) {

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
        full_name: full_name,
        group: group,
        ISU: ISU,
        number_house: number_house,
        number_room: number_room,
        date: date,
        inostran: inostran,
        note: note
    };

    let students;

    if (document.cookie.includes("students=")){
        const cookiesData = document.cookie.split("students=")[1];
        const decodedData = decodeURIComponent(cookiesData);

        students = JSON.parse(decodedData);
        if (students.some(student => student.ISU === user.ISU)){
            console.log("Такой пользователь уже есть!")
        } else {
            students.push(user);
            const data = encodeURIComponent(
                JSON.stringify(students)
            );

            document.cookie = `students=${data}`;
            console.log(students);
        }

    } else{
        students = [];
        students.push(user);
        const data = encodeURIComponent(
            JSON.stringify(students)
        );

        document.cookie = `students=${data}`;
        console.log(students);
    }

});
