<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Error page</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="header.jsp" />
    <p style="color: blue"><%= (String)request.getAttribute("model") %></p>
    <p><a href="/acs/home">Return Home</a></p>

</body>
</html>