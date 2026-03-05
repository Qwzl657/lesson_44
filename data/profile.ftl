<h1>Профиль</h1>

<p>Пользователь: ${user}</p>

<h2>Взятые книги</h2>

<ul>
<#list books as book>
    <li>
        Книга id: ${book}
        <a href="/returnBook?id=${book}">Вернуть</a>
    </li>
</#list>
</ul>

<a href="/books">Все книги</a>

<br><br>

<a href="/logout">Выйти</a>