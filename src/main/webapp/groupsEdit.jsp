<%@ page import="lv.javaguru.java2.domain.Group" %>
<%@ page import="java.util.List" %>
<%@ page import="lv.freeradiusgui.domain.User" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Group</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

</head>
<body>
    <script src="https://code.jquery.com/jquery-2.1.4.min.js"></script>
    <jsp:include page="header.jsp" />
    <h1>Edit group</h1>
    <p>Please edit data in fields. You can add or remove Access Objects from current group. Press  "Save changes" to save changes.</p>
    <div id="container">
        <div id="left">
        <%int i=0;%>
        <% Group group_id= new Group();%>
        <% List<Group> list = (List<Group>) request.getAttribute("model");
        for (Group ac_gr : list){

        %>
        <% if (i==0) {
            group_id.setgroupID(ac_gr.getgroupID());
            group_id.setaccessObjectID(ac_gr.getgroupName());
        %>
                  <form action="/acs/groups?action=update" method="post">
                  Name <br> <input type="text" name="name" style="font-weight: bold" value="<%= ac_gr.getgroupName() %>" ><br>
                  Description <br> <input type="text" name="description" value="<%= ac_gr.getdescription() %>" ><br>
                  <input type=hidden name="id" value="<%= ac_gr.getgroupID() %>" ><br>
                  <input id="button01" type="submit" value="Save changes">
                  </form>
    </div>

        <div id="right">
            <div>Group membership management. Please select record you want to add or remove from current group and press buttons below.<br><br></div>
            <table id="t01">
                <tr>
                    <th><B>Current Access ID</B></th>
                    <th>Access object Name</th>
                    <th>Access object Description</th>
                    <th>Access object Platform</th>
                    <th>Access object URL</th>
                </tr>
                <%  i++;
                    } %>
                <% if (ac_gr.getaccessObjectCount()!=1 && ac_gr.getaccessObjectName()!=null ) {
                    if (!group_id.getaccessObjectID().equals(ac_gr.getaccessObjectID())) {
                        group_id.setaccessObjectID(ac_gr.getaccessObjectID());  %>
                <tr>
                    <td><%= ac_gr.getaccessObjectID() %></td>
                    <td><%= ac_gr.getaccessObjectName() %></td>
                    <td><%= ac_gr.getaccessObjectDescription() %></td>
                    <td><%= ac_gr.getaccessObjectPlatform() %></td>
                    <td><%= ac_gr.getaccessObjectURL() %></td>
                </tr>
                <% } %>
                <% } %>
                <% } %>
            <tr>
                <th><B>Available Access ID</B></th>
                <th>Access object Name</th>
                <th>Access object Description</th>
                <th>Access object Platform</th>
                <th>Access object URL</th>
            </tr>
        <%  for (Group ac_gr : list){
            if (ac_gr.getaccessObjectCount()==1) { %>
            <tr>
                <td><%= ac_gr.getaccessObjectID() %></td>
                <td><%= ac_gr.getaccessObjectName() %></td>
                <td><%= ac_gr.getaccessObjectDescription() %></td>
                <td><%= ac_gr.getaccessObjectPlatform() %></td>
                <td><%= ac_gr.getaccessObjectURL() %></td>
            </tr>
            <% }
            } %>
        </table>
            <button id="edit" type="button" onclick="gotoSelectedId('/acs/groups?action=add_access&group=<%= group_id.getgroupID() %>&id=')">Add</button>
            <button id="delete01" type="button" onclick="gotoSelectedId('/acs/groups?action=remove&group=<%= group_id.getgroupID() %>&id=')">Remove</button>

            <div>List of users, that are members of this group:</div>
            <table id="t06">
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Surname</th>
                    <th>Position</th>
                    <th>E-mail</th>
                    <th>Created</th>
                </tr>
                <% DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm"); %>
                <% List<User> userList = (List<User>) request.getAttribute("userList");

                    for (User user : userList){
                     if (user != null){
                %>
                <tr onclick="document.location = '/acs/users?action=edit&id=<%= user.getId()%>';">
                    <td><%= user.getId() %></td>
                    <td><%= user.getName() %></td>
                    <td><%= user.getSurname() %></td>
                    <td><%= user.getPosition() %></td>
                    <td><%= user.getEmail() %></td>
                    <td><%= user.getCreationDate()==null ? "Date is Null" : user.getCreationDate().format(formatter)  %></td>
                </tr>
                <% }
                }%>
            </table>
        </div>

        <div id="footer">
        <p><a href="/acs/groups?action=view">Back to list</a> </p>
    </div>


    </div>
    <script src="js/select-row-in-table.js"></script>
</body>
</html>