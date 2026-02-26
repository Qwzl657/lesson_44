<h2>Registration</h2>

<#if error??>
    <p style="color:red">${error}</p>
</#if>

<form method="post" action="/register">
    Email: <input type="text" name="email"><br>
    Name: <input type="text" name="name"><br>
    Password: <input type="password" name="password"><br>
    <button type="submit">Register</button>
</form>