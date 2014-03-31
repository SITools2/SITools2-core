<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<#include "html.head.ftl">

<body style="font-family: sans-serif;">

<a href="${sitoolsUrl}"><img src="${sitoolsUrl}../common/res/images/logo_01_petiteTaille.png"/></a>

<p>
Dear ${user.firstName} ${user.lastName},
</p>

<p>
Your order ${order.description} is now completed <br/>
Please connect to <a href="${sitoolsUrl}">SITools2</a> to get the result.
</p>

<#include "mail.bottom.ftl">


</body>
</html>