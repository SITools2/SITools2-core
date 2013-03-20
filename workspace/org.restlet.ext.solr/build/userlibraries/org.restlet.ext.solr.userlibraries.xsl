<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:param name="output" select="'man'" />
	<xsl:param name="ROOT_DIRECTORY" select="'D:/CNES_ULISSE'" />
	<xsl:param name="LOG_DIRECTORY" select="'${HOME}'" />
	<xsl:template match='/'>
		<xsl:if test="$output = 'man'">
			<xsl:message>
				Parameters :
			</xsl:message>
			<xsl:message>
				param output values : man, ant, bat, shell, eclipse
			</xsl:message>
			<xsl:message>
				param ROOT_DIRECTORY default value D:/CNES_ULISSE
			</xsl:message>
			<xsl:message>
				param LOG_DIRECTORY default value ${HOME}
			</xsl:message>
		</xsl:if>
		<xsl:if test="$output !=  'man'">
			<xsl:message>
				<xsl:value-of select="$ROOT_DIRECTORY" />
			</xsl:message>
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<xsl:template match="/eclipse-userlibraries">
		<xsl:if test="$output='ant'">
			<xsl:message>
				Sortie vers fichier ant de classpath
			</xsl:message>
			<xsl:call-template name="output-ant" />
		</xsl:if>		
		<xsl:if test="$output ='eclipse'">
			<xsl:message>
				Sortie vers fichier eclipse local
			</xsl:message>
			<xsl:call-template name="output-eclipse" />
		</xsl:if>
	</xsl:template>

	<!-- Sortie xml ant des classpaths -->
	<xsl:template name="output-ant">
		<project name="sitools-userlibraries">
			<xsl:for-each select="library">
				<path>
					<xsl:attribute name="id"><xsl:value-of
						select="./@name" /></xsl:attribute>
					<filelist>
						<xsl:for-each select="./archive">
							<file>
								<xsl:attribute name="name"><xsl:value-of
									select="replace(@path,'D:/CNES-ULISSE', $ROOT_DIRECTORY)" /></xsl:attribute>
							</file>
						</xsl:for-each>
					</filelist>
				</path>
			</xsl:for-each>
		</project>
	</xsl:template>

	<!-- Sortie xml ant des classpaths -->
	<xsl:template name="output-eclipse">
		<xsl:copy>
			<xsl:apply-templates select="@*" />
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="archive">
		<archive>
			<xsl:attribute name="path"
				select="replace(@path,'D:/CNES-ULISSE', $ROOT_DIRECTORY)" />
		</archive>
	</xsl:template>

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*" />
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>