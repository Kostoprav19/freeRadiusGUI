<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login page</title>
    <link href="${pageContext.request.contextPath}/css/style.css" rel="StyleSheet" type="text/css" />
</head>
<body>
<div id="login">
    <h1>Access Control System</h1>
    <p>Please enter credentials:</p>
        <form action="/acs/login" method="post">
            Login name:<br>
            <input id="loginInput" type="text" name="username" value="" autofocus>
            <br>
            Password:<br>
            <input id="loginInput" type="password" name="password" value="">
            <br>
            <input id="loginButton" type="submit" value="Login">
        </form>
</div>
</body>
</html>