<h2>Login</h2>

<#if error??>
    <p style="color:red">${error}</p>
</#if>

<form method="post" action="/login">
    Email: <input type="text" name="email"><br>
    Password: <input type="password" name="password"><br>
    <button type="submit">Login</button>
</form>