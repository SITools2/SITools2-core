<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:template match="/">

	<html>
		<head>
		<title>Order records</title>
		
		</head>
			<body>
				<h1>order records</h1>
				
				
					<xsl:for-each select="list/record/attributeValues">
		
							<ul>	
							<xsl:for-each select="./attributeValue">
					
							
								<li>
								<xsl:choose>
									<xsl:when test="substring(value,1,5)='data/'">
										<xsl:value-of select="name"/> : 
										<xsl:element name="a">
										<xsl:attribute name="href">
											<xsl:value-of select="value"/>
										</xsl:attribute>
										<xsl:value-of select="value"/>
										</xsl:element>
										
									</xsl:when>
										
									<xsl:otherwise>
										<xsl:value-of select="name"/> : <xsl:value-of select="value"/>
									</xsl:otherwise>
								
								</xsl:choose>
								
								
								</li>
					
							</xsl:for-each>					
							</ul>			
					</xsl:for-each>
				
				
			</body>
			
	</html>

	</xsl:template>


</xsl:stylesheet>