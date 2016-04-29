<%@ page import="lv.javaguru.java2.domain.AccessObject" %>
<%@ page import="lv.freeradiusgui.domain.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="lv.javaguru.java2.domain.Group" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Access Objects Management</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="header.jsp" />
    <h1>Edit Access Object</h1>
    <p>Please edit data in fields. Press "Save changes" button to save changes and return to the list of access objects.</p>
        <% AccessObject ao = (AccessObject) request.getAttribute("accessObject");
        %>
    <div id="container">
        <div id="left">
        <form action="/acs/accessobjects?action=save" method="post">
            Name<br>
            <input type="text" name="name" style="font-weight: bold" value="<%= ao.getName() %>"><br>

            Description<br>
            <input type="text" name="description" value="<%= ao.getDescription() %>"><br>

            Platform<br>
            <input type="text" name="platform" value="<%= ao.getPlatform() %>"><br>

            URL<br>
            <input type="text" name="url" value="<%= ao.getUrl() %>">
            <br><br>
            <input id="button01" type="submit" value="Save changes">
        </form>
        </div>

        <div id="right">
            <div>List of users, that have access to this object:</div>
            <table>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Surname</th>
                    <th>Position</th>
                    <th>E-mail</th>
                    <th>Created</th>
                </tr>
                <% List<User> list = (List<User>) request.getAttribute("userList");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm");
                    for (User user : list){
                %>
                <tr onclick="document.location = '/acs/users?action=edit&id=<%= user.getId()%>';">
                    <td><%= user.getId() %></td>
                    <td><%= user.getName() %></td>
                    <td><%= user.getSurname() %></td>
                    <td><%= user.getPosition() %></td>
                    <td><%= user.getEmail() %></td>
                    <td><%= user.getCreationDate().format(formatter) %></td>
                </tr>
                <% } %>
            </table>
            <br><br>
            <div>List of groups, that have access to this object:</div>
            <table id="t01">
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Description</th>
                </tr>
                <% List<Group> groupList = (List<Group>) request.getAttribute("groupList");
                    for (Group group : groupList){
                %>
                <tr onclick="document.location = '/acs/groups?action=edit&id=<%= group.getgroupID()%>';">
                    <td><%= group.getgroupID() %></td>
                    <td><%= group.getgroupName() %></td>
                    <td><%= group.getdescription() %></td>
                </tr>
                <% } %>
            </table>
        </div>

        <div id="footer">
        <p><a href="/acs/accessobjects?action=view">Back to list</a> </p>
        </div>

    </div>
</body>
</html>