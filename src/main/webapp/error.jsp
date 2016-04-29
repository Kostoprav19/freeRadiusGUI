<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Error page</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h1>This is error page</h1>

    <p style="color: crimson"><%= (String)request.getAttribute("model") %></p>
    <p><a href="/acs/">Login page</a></p>

</body>
</html>