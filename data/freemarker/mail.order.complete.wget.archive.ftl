<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<#include "html.head.ftl">

<body style="font-family: sans-serif;">

<p>
Dear <i>${order.name}</i>, <br/>
your <i>${order.description}</i> order is <i>${order.status!}</i>.<br/>
<br/>
<br/>
In order to retrieve the ordered files, use the following command :<br/>
<i>wget --auth-no-challenge --http-user=${user.identifier!} --http-password=[password] ${order.resourceCollection[0]!}</i>
</p>
<p>
For further information, please contact the administrator <a href="mailto:${mail.from!}">${mail.from!}</a>.
</p>
<p>
Best regards,<br/>
The project Team.
</p>
</body>
</html>