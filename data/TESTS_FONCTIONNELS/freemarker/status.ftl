<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Expires" content="0">
	<meta http-equiv="Pragma" content="no-cache">
	
	<meta NAME="author" content="CNES">
	<meta NAME="description" content="SITools2 is an open source framework for scientific archives. It provides both search capabilities, data access and web services integration.">
	
	<meta NAME="keywords" content="CNES, SITools2, open source, web service, archive,  scientific, Data Access Layer, information system, data server, tool, open search, interoperability">
	
	<meta NAME="DC.Title" content="SITools2 - Scientific archive">
	<meta NAME="DC.Creator" content="SITools2 - CNES">
	<meta NAME="DC.Description" content="Empty - complete status.ftl">
	
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>SITOOLS STATUS PAGE</title>

	<link rel="shortcut icon" href="/sitools/common/res/images/icons/logo_fav_icone.ico" type="image/x-icon">
</head>
<#-- <#include "html.head.ftl"> -->

<body style="font-family: sans-serif;">

<h1>Sitools 2 error page</h1>
<p style="font-size: 1.2em;font-weight: bold;margin: 1em 0px;">
${description!}
</p>

<p>
${status!}
</p>

<p>You can get technical details <a href="${status.uri}">here</a>.<br>

For further assistance, you can contact the <a href="mailto:${service.contactEmail}">administrator</a>.<br>

Please log in at our <a href="${service.homeRef}">home page</a>.

</p>
</body>
</html>