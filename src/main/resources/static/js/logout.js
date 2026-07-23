console.log('Файл logout.js загружен'); // Чтобы понять, дошёл ли скрипт

document.addEventListener("DOMContentLoaded", function () {
    console.log('DOMContentLoaded сработал'); // Проверка события

    const logoutLink = document.querySelector('.logout-link');
    if (logoutLink) {
        console.log('Найден .logout-link'); // Проверка наличия элемента
        logoutLink.addEventListener('click', function (e) {
            e.preventDefault(); // ← добавить
            console.log('Функция logout() вызвана');
            const form = document.getElementById('logout-form');
            if (form) {
                form.submit();
            } else {
                console.error('Form #logout-form not found');
            }
        });
    } else {
        console.warn('Элемент .logout-link не найден');
    }
});