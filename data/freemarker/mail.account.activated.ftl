<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<#include "html.head.ftl">

<body style="font-family: sans-serif;">

<a href="${sitoolsUrl}"><img src="${sitoolsUrl}../common/res/images/logo_01_petiteTaille.png"/></a>

<p>
Dear ${user.firstName} ${user.lastName},
</p>

<p>
Thank you for registration at <a href="${sitoolsUrl}">SITools2</a><br/>
Your registration was successful.
</p>

<#include "mail.bottom.ftl">

</body>
</html>
