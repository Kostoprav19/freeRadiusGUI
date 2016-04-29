<%@ page import="lv.javaguru.java2.domain.AccessObject" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Access Objects Management</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

</head>
<body>
<script src="https://code.jquery.com/jquery-2.1.4.min.js"></script>
    <jsp:include page="header.jsp" />
    <h1>Access Objects Management</h1>

    <a href="/acs/accessobjects?action=add"><button type="button" class="button">Add</button></a>
    <button id="edit" type="button" onclick="gotoSelectedId('/acs/accessobjects?action=edit&id=')">Edit</button>
    <button id="delete01" type="button" onclick="gotoSelectedIdIfSure('/acs/accessobjects?action=delete&id=', 'Are you sure?')">Delete</button></a>

    <table id="t01" style="width: auto">
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Description</th>
            <th>Platform</th>
            <th>URL</th>
        </tr>
        <% List<AccessObject> list = (List<AccessObject>) request.getAttribute("model");

        for (AccessObject ao : list){
        %>
        <tr>
            <td><%= ao.getAccessObjectId() %></td>
            <td><%= ao.getName() %></td>
            <td><%= ao.getDescription() %></td>
            <td><%= ao.getPlatform() %></td>
            <td><%= ao.getUrl() %></td>
        </tr>
        <% } %>
    </table>
<script src="js/select-row-in-table.js"></script>
</body>
</html>