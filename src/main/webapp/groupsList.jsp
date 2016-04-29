<%@ page import="lv.javaguru.java2.domain.Group" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Groups</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

</head>
<body>
<script src="https://code.jquery.com/jquery-2.1.4.min.js"></script>
    <jsp:include page="header.jsp" />
    <h1>Groups Management</h1>

    <a href="/acs/groups?action=add"><button type="button" class="button">Add</button></a>
    <button id="edit" type="button" onclick="gotoSelectedId('/acs/groups?action=edit&id=')">Edit</button>
    <button id="delete01" type="button" onclick="gotoSelectedIdIfSure('/acs/groups?action=delete&id=', 'Are you sure?')">Delete</button></a>

    <table id="t01" style="width: auto">
        <tr>
            <th width="5%">ID</th>
            <th width="25%">Name</th>
            <th width="40%">Description</th>
            <th width="30%">Number of Access objects</th>
            <!--<th>Access object ID</th>
            <th>Access object Name</th>
            <th>Access object Description</th>
            <th>Access object Platform</th>
            <th>Access object URL</th>-->
        </tr>
        <% List<Group> list = (List<Group>) request.getAttribute("model");

        for (Group gr : list){
        %>
        <tr>
            <td><%= gr.getgroupID() %></td>
            <td><%= gr.getgroupName() %></td>
            <td><%= gr.getdescription() %></td>
            <td><%= gr.getaccessObjectCount() %></td>
           <!-- <td><%= gr.getaccessObjectID() %></td>
            <td><%= gr.getaccessObjectName() %></td>
            <td><%= gr.getaccessObjectDescription() %></td>
            <td><%= gr.getaccessObjectPlatform() %></td>
            <td><%= gr.getaccessObjectURL() %></td> -->
        </tr>
        <% } %>
    </table>
<script src="js/select-row-in-table.js"></script>
</body>
</html>