<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<#include "html.head.ftl">

<body style="font-family: sans-serif;">

<h1>Sitools 2 information</h1>
<p style="font-size: 1.2em;font-weight: bold;margin: 1em 0px;">
${description!}
</p>

<p>
Your new account is now available
<ul>
<li>Login : ${user.identifier}</li>
<li>Password : ${pass!}</li>
</ul>
</p>

</body>
</html>