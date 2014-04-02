<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<#include "html.head.ftl">

<body style="font-family: sans-serif;">

<a href="${sitoolsUrl}"><img src="${sitoolsUrl}../common/res/images/logo_01_petiteTaille.png"/></a>

<p>
Dear ${order.name}, <br/>
your order ${order.description} is ${order.status!}.<br/>
<br/>
<br/>
In order to retrieve the ordered files, use the following command :<br/>
<i>wget --auth-no-challenge --http-user=${user.identifier!} --http-password=[password] ${order.resourceCollection[0]!}</i>
</p>

<#include "mail.bottom.ftl">
</body>
</html>