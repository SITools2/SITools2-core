<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<#include "html.head.ftl">

<body style="font-family: sans-serif;">


<p>
Dear ${user.firstName} ${user.lastName},
</p>

<p>
We are pleased to inform you that your user account has been created on SITools2. You can can access data as defined in the <a href="${sitoolsUrl}">SITools2 url</a>.
</p>

<p>
Your login is ${user.identifier}<br/>
Your password is initialized with ${pass}, please update it as soon as possible with "Edit Profile" Menu.
</p>


<p>
Best regards,<br/>
The SITools2 team</p>

</body>
</html>