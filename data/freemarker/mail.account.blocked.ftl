<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<#include "html.head.ftl">

<body style="font-family: sans-serif;">

<a href="${sitoolsUrl}"><img src="${sitoolsUrl}../common/res/images/logo_01_petiteTaille.png"/></a>

<p>
Dear administrator,
</p>

<p>
The account of the user <b>${userId}</b> on <a href="${sitoolsUrl}">SITools2</a> has been blocked because of too many bad connection attemps.<br/>
You can unlock his account by connecting to SITools2 administration page.
</p>


<#include "mail.bottom.ftl">

</body>
</html>

