<%@ page import="lv.javaguru.java2.domain.*" %>
<%@ page import="lv.freeradiusgui.domain.User" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <title>Add user</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="header.jsp" />
<h1>Add user</h1>


<div>
<form action="/acs/users?action=save" method="post">
  Name:  <br>   <input type="text" name="name" value=""><br>
  Surname:   <br><input type="text" name="surname" value=""><br>
  Position:  <br><input type="text" name="position" value=""><br>
  E-mail:    <br><input type="text" name="email" value=""><br><br>
  Password:    <br><input type="text" name="password" value=""><br><br>
  <input type="submit" value="Add user">
</form>
</div>

<p><a href="/acs/users?action=view">Back to list</a> </p>

</body>
</html>