<div id="header">
    <div style="position:absolute; left:5px; top:5px;">
        <div id="header-name" >Access Control System v0.4</div> |
        <a href="/acs/home">Home</a> |
        <a href="/acs/users">Users</a> |
        <a href="/acs/accessobjects">Access objects</a> |
        <a href="/acs/groups">Groups</a> |
        <a href="/acs/logs">Audit logs</a> |

    </div>
    <div id="header-username">Hello, <%= (String)session.getAttribute("username") %></div>
    <div id="header-logout"><a href="/acs/login?action=logout">Logout</a></div>
</div>