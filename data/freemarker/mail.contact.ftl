<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<#include "html.head.ftl">

<body style="font-family: sans-serif;">

<a href="${sitoolsUrl}"><img src="${sitoolsUrl}../common/res/images/logo_01_petiteTaille.png"/></a>

<p>
Dear administrator,
</p>

<p>
${contact.name} has sent you a message using the contact form:<br/>
<blockquote><pre>${contact.body}</pre></blockquote><br/><br/>
${contact.name} (${contact.email})
</p>

<#include "mail.bottom.simple.ftl">

</body>
</html>