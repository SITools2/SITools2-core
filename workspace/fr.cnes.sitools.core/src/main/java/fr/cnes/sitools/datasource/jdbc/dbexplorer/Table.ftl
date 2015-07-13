<html>
<head>
<title>Table definition</title>
</head>
<body>
<table>
	<tbody>
		<tr>
			<td>Name:</td>
			<td>${name}</td>
		</tr>
		<tr>
			<td>URL:</td>
			<td><a href="${url}">${url}</a></td>
		</tr>
	</tbody>
</table>
Attributes
<table>
	<tbody>
		<tr>
			<td>Name</td>
			<td>Type</td>
			<td>Size</td>
		</tr>
	<#list attributes as att>
		<tr>
			<td>${att.name}</td>
			<td>${att.type}</td>
			<td>${att.size}</td>
		</tr>
	</#list> 	
	</tbody>
</table>
</body>
</html>
