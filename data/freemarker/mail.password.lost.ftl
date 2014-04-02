<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<#include "html.head.ftl">

<body style="font-family: sans-serif;">

<a href="${sitoolsUrl}"><img src="${sitoolsUrl}../common/res/images/logo_01_petiteTaille.png"/></a>

<p>

Dear ${user.firstName} ${user.lastName}, 
<br/>
<br/>

You have asked to reinitialize your password for your account <a href="${sitoolsUrl}">SITools2</a>.<br/>
In order to modify you password, please click on the following link <a href="${passwordLostUrl}"/>Change your password</a><br/>
<br/>
If the link does not work, just copy it into your favorite browser<br/>
<i><small>${passwordLostUrl}</small></i>
<br/>
</p>
<#include "mail.bottom.ftl">

</body>
</html>