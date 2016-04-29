<%@ page import="lv.freeradiusgui.domain.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <title>Users Management</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <script src="https://code.jquery.com/jquery-2.1.4.min.js"></script>
    <jsp:include page="header.jsp" />
    <h1>User Management</h1>

    <a href="/acs/users?action=add"><button type="button" class="button">Add user</button></a>
    <button id="edit" type="button" onclick="gotoSelectedId('/acs/users?action=edit&id=')">Edit</button>
    <button id="delete01" type="button" onclick="gotoSelectedIdIfSure('/acs/users?action=delete&id=', 'Are you sure?')">Delete</button></a>
    <table id="t01" style="width: auto">
      <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Surname</th>
        <th>Position</th>
        <th>E-mail</th>
        <th>Created</th>
      </tr>
      <% List<User> list = (List<User>) request.getAttribute("model");
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm");

        for (User user : list){
       %>
      <tr>
        <td><%= user.getId() %></td>
        <td><%= user.getName() %></td>
        <td><%= user.getSurname() %></td>
        <td><%= user.getPosition() %></td>
        <td><%= user.getEmail() %></td>
        <td><%= user.getCreationDate().format(formatter) %></td>
      </tr>
      <% } %>
    </table>


<script src="js/select-row-in-table.js"></script>
</body>
</html>