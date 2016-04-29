<%@ page import="lv.javaguru.java2.domain.LogRecord" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Audit Logs</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="header.jsp" />
    <h1>Audit logs</h1>

    <button id="delete" type="button" onclick="goToUrlIfSure('/acs/logs?action=clear', 'All log records will be deleted. Are you sure? ');">Delete all records</button></a>
    <button id="delete" type="button" onclick="goToUrl('/acs/logs?page=1&sortOrder=ascending');">Sort ascending</button></a>
    <button id="delete" type="button" onclick="goToUrl('/acs/logs?page=1&sortOrder=descending');">Sort descending</button></a>

    <div style="margin-bottom: 10px">
        <% Integer currentPage = (Integer) request.getAttribute("currentPage");
            Integer totalPages = (Integer) request.getAttribute("totalPages");
        %>
        <a href="/acs/logs?page=<%= currentPage-1%>&sortOrder=<%= request.getParameter("sortOrder")%>"><- previous page</a>
        <b>Page <%= currentPage%> of <%= totalPages%> </b>
        <a href="/acs/logs?page=<%= currentPage+1%>&sortOrder=<%= request.getParameter("sortOrder")%>">next page -></a>
    </div>

    <table id="t01" style="width: auto;">
        <tr>
            <th width="10%">Date/Time</th>
            <th width="10%">User</th>
            <th width="10%">IP</th>
            <th width="25%">Command</th>
            <th width="25%">Value</th>
        </tr>
        <% List<LogRecord> list = (List<LogRecord>) request.getAttribute("list");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm");
        for (LogRecord log : list){
        %>
        <tr>
            <td><%= log.getDate().format(formatter) %></td>
            <td><%= log.getUserName() %></td>
            <td><%= log.getIp() %></td>
            <td><%= log.getCommand() %></td>
            <td><%= log.getValue() %></td>
        </tr>
        <% } %>
    </table>

    <div style="margin-top: 10px">
        <a href="/acs/logs?page=<%= currentPage-1%>&sortOrder=<%= request.getParameter("sortOrder")%>"><- previous page</a>
        <b>Page <%= currentPage%> of <%= totalPages%> </b>
        <a href="/acs/logs?page=<%= currentPage+1%>&sortOrder=<%= request.getParameter("sortOrder")%>">next page -></a>
    </div>
    <script src="js/buttons-functions.js"></script>
</body>
</html>