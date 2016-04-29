<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Group</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

</head>
<body>
    <jsp:include page="header.jsp" />
         <h1>Create Group</h1>
          <div>
          <form action="/acs/groups?action=save" method="post">
              Name <br> <input type="text" name="name" value="">
              <br>
              Description <br> <input type="text" name="description" value="">
              <br><br>
              <input id="button01" type="submit" value="Add">
          </form>
          </div>
          <p style="color: blue"><%= request.getAttribute("model")!=null ? (String)request.getAttribute("model") : "" %></p>
          <p><a href="/acs/groups?action=view">Back to list</a> </p>
 </body>
 </html>