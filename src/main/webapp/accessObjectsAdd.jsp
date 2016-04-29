<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Access Objects Management</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="header.jsp" />
    <h1>Add new Access Objects</h1>
    <div>
    <form action="/acs/accessobjects?action=save" method="post">
        Name <br> <input type="text" name="name" value="">
        <br>
        Description <br> <input type="text" name="description" value="">
        <br>
        Platform <br> <input type="text" name="platform" value="">
        <br>
        URL <br> <input type="text" name="url" value="">
        <br><br>
        <input id="button01" type="submit" value="Add">
    </form>
    </div>
    <p><a href="/acs/accessobjects?action=view">Back to list</a> </p>
</body>
</html>