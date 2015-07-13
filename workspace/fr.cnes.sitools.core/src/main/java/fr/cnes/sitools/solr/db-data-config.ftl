<dataConfig>
	<dataSource type="JdbcDataSource" driver="${datasource.driverClass}"
		url="${datasource.url}" user="${datasource.userLogin}" password="${datasource.userPassword}" />
	<document name="${document}">
		<#list entities as ents>
		<entity name="${ents.name}" query="${ents.query}" transformer="TemplateTransformer">
			<#list ents.fields as flds>
				<field column="${flds.column}" name="${flds.name}" <#if flds.template??> template="${flds.template}"</#if>/>
			</#list>
		</entity>
		</#list>
	</document>
</dataConfig>