<html>
<head>
<title>Database content</title>
</head>
<body>
<table>
	<tbody>
		<tr>
			<td>URL:</td>
			<td><a href="${url}">${url}</a></td>
		</tr>
	</tbody>
</table>
Tables
<table>
	<tbody>
		<tr>
			<td>Name</td>
			<td>Url</td>
		</tr>
	<#list tables as tb>
		<tr>
			<td>${tb.name}</td>
			<td><a href="${tb.url}">${tb.url}</a></td>
		</tr>
	</#list> 	
	</tbody>
</table>
</body>
</html>
