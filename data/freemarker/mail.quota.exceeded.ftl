<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<#include "html.head.ftl">

<body style="font-family: sans-serif;">

<a href="${sitoolsUrl}"><img src="${sitoolsUrl}../common/res/images/logo_01_petiteTaille.png"/></a>

<p>

Dear ${storage.userId}, 
<br/>

<p>
Your userspace is full : ${ storage.storage.busyUserSpace / storage.storage.quota * 100} % used <br/>
Please connect to <a href="${sitoolsUrl}">SITools2</a> to clean it.
</p>

</p>

<#include "mail.bottom.ftl">

</body>
</html>