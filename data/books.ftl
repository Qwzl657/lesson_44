<h1>Books</h1>

<ul>
<#list books as book>
    <li>
        <a href="/book?id=${book.id}">
            ${book.title} — ${book.author}
        </a>
        (${book.status})
    </li>
</#list>
</ul>