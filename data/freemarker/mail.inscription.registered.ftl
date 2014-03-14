<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<#include "html.head.ftl">

<body style="font-family: sans-serif;">

<h1>Sitools 2 information</h1>
<p style="font-size: 1.2em;font-weight: bold;margin: 1em 0px;">
${description!}
</p>

<p>
New user registration
<ul>
	<li>Login : ${inscription.identifier}</li>
	<li>FirstName : ${inscription.firstName}</li>
	<li>LastName : ${inscription.lastName}</li>
	<li>Email : ${inscription.email}</li>
</ul>

To validate inscription go to the <a href="${sitoolsUrl}">SITools2 adminstration</a>
</p>

</body>
</html>