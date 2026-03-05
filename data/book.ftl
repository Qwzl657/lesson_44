<h1>Список книг</h1>

<ul>
<#list books as book>
<li>
${book.title}

<a href="/giveBook?id=${book.id}">
Взять
</a>

</li>
</#list>
</ul>