<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Access Control System Home</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

</head>
<div>
<jsp:include page="header.jsp" />
<h1>Main page</h1>
<div style="font-size: larger">
<p>Please choose what you want to do:</p>
<p>You can manage users records and in <a href="/acs/users">Users section</a>.</p>
<p>In <a href="/acs/accessobjects">Access objects section</a> you can create new, modify and delete access objects.</p>
<p>If you want manage group, please proceed to <a href="/acs/groups">Groups section</a>.</p>
<p>All changes are logged, you can view logs here: <a href="/acs/logs">Audit logs</a>.</p>
</div>
<script src="js/buttons-functions.js"></script>
</body>
</html>