function validate() {
    const full_name = document.getElementById('full-name').value.trim();
    const group = document.getElementById('group').value.trim();
    const ISU = document.getElementById('ISU').value.trim();
    const date = document.getElementById('date').value.trim();
    const params = new URLSearchParams(window.location.search);
    const editId = Number(params.get('id'));
    clearErrors();

    if (!/^[a-zA-Zа-яА-ЯёЁ\s]+$/.test(full_name)) {
        showError('full-name', 'ФИО должно состоять только из букв');
        return false;
    }

    if (!/^[a-zA-Zа-яА-ЯёЁ][0-9]{4,5}$/.test(group)) {
        showError('group', 'Группа должна начинаться с буквы, затем 4-5 цифр');
        return false;
    }

    if (isNaN(ISU) || ISU === '') {
        showError('ISU', 'ИСУ может состоять только из цифр');
        return false;
    }

    if (ISU[0] === '0') {
        showError('ISU', 'ИСУ не может начинаться с нуля');
        return false;
    }

    const students = getStudentsFromCookie();
    const isuNumber = Number(ISU);
    const sameIsuStudent = students.find(function(student) {
        return Number(student.ISU) === isuNumber && Number(student.id) !== editId;
    });

    if (sameIsuStudent) {
        showError('ISU', 'Студент с таким ИСУ уже существует');
        return false;
    }


    let year = date.substring(0, 4);
    if (year < '2016') {
        showError('date', 'Вы не могли заселиться в такую дату, или вы уже не студент');
        return false;
    }

    let today = new Date().toISOString().split('T')[0];
    if (date > today) {
        showError('date', 'Дата заселения не может быть в будущем');
        return false;
    }

    return true;
}

function getStudentsFromCookie() {
    if (!document.cookie.includes('students=')) {
        return [];
    }

    const cookiesData = document.cookie.split('students=')[1].split(';')[0];
    const decodedData = decodeURIComponent(cookiesData);
    return JSON.parse(decodedData);
}

function showError(fieldId, message) {
    const field = document.getElementById(fieldId);
    if (!field) return;

    field.classList.add('input-error');

    let errorContainer = field.parentElement.querySelector('.field-error');
    if (!errorContainer) {
        errorContainer = document.createElement('span');
        errorContainer.className = 'field-error';
        field.parentElement.appendChild(errorContainer);
    }
    errorContainer.textContent = message;
}

function clearErrors() {
    document.querySelectorAll('.input-error').forEach(el => {
        el.classList.remove('input-error');
    });
    document.querySelectorAll('.field-error').forEach(el => {
        el.remove();
    });
}

