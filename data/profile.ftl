<h1>Профиль сотрудника</h1>

<p><b>ФИО:</b> ${employee.fullName}</p>
<p><b>ID:</b> ${employee.id}</p>

<h2>Текущие книги</h2>
<ul>
<#list currentBooks as book>
    <li>
        ${book.title}
        <a href="/returnBook?id=${book.id}">Вернуть</a>
    </li>
</#list>
</ul>

<h2>История книг</h2>
<ul>
<#list pastBooks as book>
    <li>${book.title}</li>
</#list>
</ul>

<a href="/books">Все книги</a>
<br>
<a href="/logout">Выйти</a>