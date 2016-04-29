<%@ taglib prefix="http" uri="http://www.springframework.org/tags/form" %>
<%@ page import="lv.javaguru.java2.domain.*" %>
<%@ page import="lv.freeradiusgui.domain.User" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <title>Add user</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
  <script src="https://code.jquery.com/jquery-2.1.4.min.js"></script>
  <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
</head>
<body>
<jsp:include page="header.jsp" />
<h1>Edit user</h1>
<p>Please edit data in fields. Press "Save changes" button to save changes and return to the list of access objects.</p>
<% User user = (User) request.getAttribute("user");
%>
<div id="container">
  <div id="left">
  <form action="/acs/users?action=save" method="post">
    Name<br>
    <input type="text" name="name" style="font-weight: bold" value="<%= user.getName() %>"><br>
    Surname<br>
    <input type="text" name="surname" style="font-weight: bold" value="<%= user.getSurname() %>"><br>
    Position<br>
    <input type="text" name="position" value="<%= user.getPosition() %>"><br>
    E-mail<br>
    <input type="text" name="email" value="<%= user.getEmail() %>"><br>
    Password<br>
    <input type="text" name="password" value="<%= user.getPassword() %>">
    <br><br>
    <input id="button01" type="submit" value="Save changes">
  </form>
  </div>

  <div id="right">
    <div>List of Access Objects:</div>
    <table id="t01">
      <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Description</th>
        <th>Platform</th>
        <th>URL</th>
      </tr>
      <% List<AccessObject> list = (List<AccessObject>) request.getAttribute("aoList");

        for (AccessObject ao : list){
            if (ao != null){
      %>
      <tr>
        <td><%= ao.getAccessObjectId() %></td>
        <td><%= ao.getName() %></td>
        <td><%= ao.getDescription() %></td>
        <td><%= ao.getPlatform() %></td>
        <td><%= ao.getUrl() %></td>
      </tr>
      <% }
      }%>
    </table>
    <button type="button" onclick="openModalWindow('dialog01')">Add</button>
    <button type="button" id="delete01"  onclick="goToUrlWithId('/acs/users?action=edit&id=<%= request.getParameter("id")%>&removeAccessObject=')">Remove</button>

    <br><br>
    <div>List of Groups:</div>
    <table id="t02">
      <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Description</th>
      </tr>
      <% List<Group> grList = (List<Group>) request.getAttribute("grList");

        for (Group g : grList){
      %>
      <tr>
        <td><%= g.getgroupID()%></td>
        <td><%= g.getgroupName()%></td>
        <td><%= g.getdescription() %></td>
      </tr>
      <% } %>
    </table>
    <button type="button" onclick="openModalWindow('dialog02')">Add</button>
    <button type="button" id="delete02" onclick="goToUrlWithId('/acs/users?action=edit&id=<%= request.getParameter("id")%>&removeGroup=')">Remove</button>
  </div>
</div>

  <div id="footer">
  <p><a href="/acs/users?action=view">Back to list</a> </p>

    <dialog id="dialog01">
      <p>Select Access Object you wan to add:</p>
      <table id="t03">
        <tr>
          <th>ID</th>
          <th>Name</th>
          <th>Description</th>
          <th>Platform</th>
          <th>URL</th>
        </tr>
        <%
          List<AccessObject> fullAoList = (List<AccessObject>) request.getAttribute("fullAoList");
          for (AccessObject ao : fullAoList){
            if (ao != null){
        %>
        <tr>
          <td><%= ao.getAccessObjectId() %></td>
          <td><%= ao.getName() %></td>
          <td><%= ao.getDescription() %></td>
          <td><%= ao.getPlatform() %></td>
          <td><%= ao.getUrl() %></td>
        </tr>
        <% }
        }%>
      </table>

      <button id="add03" type="button" onclick="goToUrlWithId('/acs/users?action=edit&id=<%= request.getParameter("id")%>&addAccessObject=')">Add</button>
      <button id="cancel" type="button" onclick="goToUrl('/acs/users?action=edit&id=<%= request.getParameter("id")%>')">Cancel</button>
    </dialog>

    <dialog id="dialog02">
      <p>Select Group you want to add:</p>
      <table id="t04">
        <tr>
          <th>ID</th>
          <th>Name</th>
          <th>Description</th>
        </tr>
        <%
          List<Group> fullGrList = (List<Group>) request.getAttribute("fullGrList");
          for (Group g : fullGrList){
            if (g != null){
        %>

        <tr>
          <td><%= g.getgroupID()%></td>
          <td><%= g.getgroupName()%></td>
          <td><%= g.getdescription() %></td>
        </tr>
        <% }
        }%>
      </table>

      <button id="add04" type="button" onclick="goToUrlWithId('/acs/users?action=edit&id=<%= request.getParameter("id")%>&addGroup=')">Add</button>
      <button id="cancel" type="button" onclick="goToUrl('/acs/users?action=edit&id=<%= request.getParameter("id")%>')">Cancel</button>
    </dialog>
  </div>

</div>

<script src="js/buttons-functions.js"></script>
<script src="js/select-row-in-table.js"></script>
</body>
</html>