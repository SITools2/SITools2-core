<?xml version='1.0' encoding='UTF-8'?>

<!-- * Licensed to the Apache Software Foundation (ASF) under one or more 
	* contributor license agreements. See the NOTICE file distributed with * 
	this work for additional information regarding copyright ownership. * The 
	ASF licenses this file to You under the Apache License, Version 2.0 * (the 
	"License"); you may not use this file except in compliance with * the License. 
	You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 
	* * Unless required by applicable law or agreed to in writing, software * 
	distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT 
	WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the 
	License for the specific language governing permissions and * limitations 
	under the License. -->

<!-- Simple transform of Solr query results to RSS -->

<xsl:stylesheet version='1.0'
	xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>

	<xsl:output method="xml" encoding="utf-8" />
	<xsl:template match='/'>
		<rss version="2.0" xmlns:opensearch="http://a9.com/-/spec/opensearch/1.1/"
			xmlns:atom="http://www.w3.org/2005/Atom">
			<channel>
				<title>${datasetName!}</title>
				<link>${datasetURI!}</link>
				<description>
					${datasetDescription!}
         	</description>
				<language>en-us</language>
				<opensearch:totalResults>
					<xsl:value-of select="response/result/@numFound" />
				</opensearch:totalResults>
				<opensearch:startIndex>
					<xsl:value-of select="response/result/@start" />
				</opensearch:startIndex>
				<opensearch:itemsPerPage>
					<xsl:value-of select="count(response/result/doc)" />
				</opensearch:itemsPerPage>
				<xsl:apply-templates select="response/result/doc" />
			</channel>
		</rss>
	</xsl:template>

	<!-- search results xslt -->
	<!-- <xsl:template match="doc"> -->
	<!-- <item> -->
	<!-- <title><xsl:value-of select="str[@name='${title!}']"/></title> -->
	<!-- <link> -->
	<!-- <xsl:value-of select="str[@name='${link!}']"/> -->
	<!-- </link> -->
	<!-- <description> -->
	<!-- <xsl:value-of select="str[@name='${description!}']"/> -->
	<!-- </description> -->
	<!-- <pubDate><xsl:value-of select="str[@name='${pubDate!}']"/></pubDate> -->
	<!-- <guid> -->
	<!-- <xsl:value-of select="str[@name='${guid!}']"/> -->
	<!-- </guid> -->
	<!-- </item> -->
	<!-- </xsl:template> -->


	<!-- search results xslt -->
	<xsl:template match="doc">
		<xsl:variable name="pkvalue" select="str[@name='${uniqueKey!}']">
		</xsl:variable>
		<item>
			<title>
				<xsl:choose>
					<xsl:when
						test="/response/lst/lst[@name=$pkvalue]/arr[@name='${title!}']/*">
						<xsl:value-of
							select="/response/lst/lst[@name=$pkvalue]/arr[@name='${title!}']/*" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="*[@name='${title!}']" />
					</xsl:otherwise>
				</xsl:choose>
			</title>
			<#if link??>
				<link>
					<xsl:value-of select="*[@name='${link!}']" />
				</link>
			</#if>	
			<#if description??>
			<description>
				<xsl:value-of select="*[@name='${description!}']" />
			</description>
			</#if>	
			<#if pubDate??>
			<pubDate>
				<xsl:value-of select="*[@name='${pubDate!}']" />
			</pubDate>
			</#if>
			<guid>
				<xsl:value-of select="*[@name='${guid!}']" />
			</guid>
		</item>
	</xsl:template>

</xsl:stylesheet>
