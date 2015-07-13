<dataConfig>
	<script><![CDATA[
				id = 1;
				function GenerateId(row) {
					row.put('id', (id ++).toFixed());
					return row;
				}       
		]]></script>

    <dataSource type="BinURLDataSource" name="data"/>
    <dataSource type="URLDataSource" baseUrl="${xpathdatasource}/" name="main"/>
	<document name="${document}">
        <entity name="rec" processor="XPathEntityProcessor" url="data.xml" forEach="/albums/album" dataSource="main" transformer="script:GenerateId">
            <field column="title" xpath="//title" />
            <field column="description" xpath="//description" />
            <entity processor="TikaEntityProcessor" url="${localdirectoryurl}/${rec.url}" dataSource="data">
                <field column="text" name="content" />
                <field column="Author" name="author" meta="true" />
                <field column="title" name="title" meta="true" />
            </entity>
        </entity>
	</document>
</dataConfig>