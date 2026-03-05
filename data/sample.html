<!doctype html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0"
        name="viewport">
    <meta content="ie=edge" http-equiv="X-UA-Compatible">
    <title>Freemarker Sample Page</title>
    <link href="css/freemarker.css" rel="stylesheet">
</head>

<body>
    <div class="container">
        <p>Привет ${user.firstName}!</p>
        <p>Сегодня ${currentDateTime}
        </p>
        <h1>FreeMarker sample page</h1>
        <div>
            <h2>Интерполяции:</h2>
            <p>
                ${r"${user}"}: ${user}
            </p>
            <p>
                ${r"${user.firstName}"}: ${user.firstName}
            </p>
            <p>
                ${r"${user.lastName}"}: ${user.lastName}
            </p>
            <p>
                ${r"${currentDateTime}"}: ${currentDateTime}
            </p>
            <p>
                ${r"${currentDateTime.year}"}: ${currentDateTime.year}
                <i>(в формате с учетом региональных настроек)</i>
            </p>
        </div>
        <div>
            <h3>Если элемент с левой стороны не задан или отсутствует его значение, то подставляется правая часть.</h3>
            <p>
                ${r'${user.middleName<b>!"No middle name defined"</b>}'}: ${user.middleName!"No middle name defined"}
            </p>
            <p>
                ${r'${user.middleName<b>!user.lastName</b>}'}: ${user.middleName!user.lastName}
            </p>
            <p>
                ${r'${user.middleName<b>!user.lastName!"нет отчества и фамилии"</b>}'}: ${user.middleName!user.lastName!"нет
            отчества и фамилии"}
            </p>
        </div>
        <div>
            <h3>Основные встроенные строковые функции:</h3>
            <p>
                ${r"${user.firstName<b>?upper_case</b>}"}: ${user.firstName?upper_case}
            </p>
            <p>
                ${r"${user.firstName<b>?length</b>}"}: ${user.firstName?length}
            </p>
            <p>
                ${r"${currentDateTime.year<b>?c</b>}"}: ${currentDateTime.year?c}
                <i>(в компьютерочитаемом формате)</i>
            </p>
            <p><a href="https://freemarker.apache.org/docs/dgui_quickstart_template.html#autoid_7">Краткий список встроенных функций</a></p>
            <p><a href="https://freemarker.apache.org/docs/ref_builtins.html">Полный список встроенных функций</a></p>
        </div>
        <div>
            <h3>Функции возвращающие логические значения и преобразование их в строку:</h3>
            <p>
                ${r'${user.emailConfirmed<b>?string("подтверждён", "не подтверждён")</b>}'}:
                ${user.emailConfirmed?string("подтверждён", "не подтверждён")}
            </p>
            <p>
                ${r'${user.firstName<b>?starts_with("A")</b>?string("yes", "no")}'}:
                ${user.firstName?starts_with("A")?string('yes', 'no')}
            </p>
            <p>
                ${r'${user.firstName<b>?starts_with("B")</b>?string("yes", "no")}'}:
                ${user.firstName?starts_with("B")?string('yes', 'no')}
            </p>
        </div>
        <h2>FTL тэги и директивы:</h2>
        <h3>Условия:</h3>
        <#if user??>
            <p>Привет ${user}!</p>
        </#if>
        <#if user.emailConfirmed>
            Ваш адрес почты подтверждён!
            <#else>
                Нажмите <a href="#">сюда</a> для подтверждения почты
        </#if>
        <h3>Работа со списками:</h3>
        <ul>
            <#list customers as customer>
                <li class="${customer?item_parity}">
                    ${customer.firstName} ${customer.email}
                </li>
            </#list>
        </ul>
        <div class="table flex flex-col">
            <div class="header flex">
                <div class="column">
                    ##
                </div>
                <div class="column">
                    Имя
                </div>
                <div class="column">
                    Фамилия
                </div>
                <div class="column">
                    Отчество
                </div>
                <div class="column">
                    Подтверждение почты
                </div>
                <div class="column">
                    Четность строки
                </div>
            </div>
            <#list customers as customer>
                <div class="row ${customer?item_parity} flex">
                    <div class="column">
                        ${customer?counter}
                    </div>
                    <div class="column">
                        ${customer.firstName!"-- не указано --"}
                    </div>
                    <div class="column">
                        ${customer.lastName!"-- не указано --"}
                    </div>
                    <div class="column">
                        ${customer.middleName!"-- не указано --"}
                    </div>
                    <div class="column">
                        ${customer.emailConfirmed?string("подтверждён", "не подтверждён")}
                    </div>
                    <div class="column">
                        ${customer?item_parity}
                    </div>
                </div>
            </#list>
        </div>
</body>

</html>