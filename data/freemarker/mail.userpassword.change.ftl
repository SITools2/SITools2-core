<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<#include "html.head.ftl">

<body style="font-family: sans-serif;">


<p style="font-size: 1.2em;font-weight: bold;margin: 1em 0px;">
${description!}
</p>

<p>
Hello ${user.firstName} ${user.lastName},
</p>

<p>
The password of your account on ${sitoolsUrl} has been changed successfully.
</p>

<p>
If you did not initiate this change, we invite you to reset your password by selecting the "forgotten password" option in the connection component.
</p>

<p>Best regards</p>
<p>The SITools2 team</p>

</body>
</html>

