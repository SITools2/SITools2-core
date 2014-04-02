<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<#include "html.head.ftl">

<body style="font-family: sans-serif;">

<a href="${sitoolsUrl}"><img src="${sitoolsUrl}../common/res/images/logo_01_petiteTaille.png"/></a>

<p>
Dear administrator,
</p>

<p>
${inscription.firstName} ${inscription.lastName} is asking for registration. <br/>
Please connect to <a href="${sitoolsUrl}">SITools2</a> to validate his registration.
</p>

<#include "mail.bottom.ftl">


</body>
</html>