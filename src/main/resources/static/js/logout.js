console.log('Файл logout.js загружен'); // Чтобы понять, дошёл ли скрипт

document.addEventListener("DOMContentLoaded", function () {
    console.log('DOMContentLoaded сработал'); // Проверка события

    const logoutLink = document.querySelector('.logout-link');
    if (logoutLink) {
        console.log('Найден .logout-link'); // Проверка наличия элемента
        logoutLink.addEventListener('click', function () {
            console.log('Функция logout() вызвана');
            alert('Выход из системы...');
            document.getElementById('logout-form').submit();
        });
    } else {
        console.warn('Элемент .logout-link не найден');
    }
});