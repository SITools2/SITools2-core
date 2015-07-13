<dataConfig>
<script><![CDATA[
        id = 1;
        function GenerateId(row) {
            row.put('id', (id ++).toFixed());
            return row;
        }
       ]]></script>
    <dataSource type="BinFileDataSource" name="bin" />
    <document name="${document}">
      <entity name="sd"
        processor="FileListEntityProcessor"
        fileName="${fileName}"
        baseDir="${baseDir}"
        recursive="true"
        rootEntity="false"
		onError="skip"
 		transformer="script:GenerateId"
 
		<#if newerThan??> newerThan="${newerThan}"</#if>
        <#if olderThan??> olderThan="${olderThan}"</#if>
		<#if biggerThan??> biggerThan=${biggerThan}</#if>
		<#if smallerThan??> smallerThan="${smallerThan}"</#if>
          >
          
            <field column="fileAbsolutePath" name="path" />
            <field column="fileSize" name="size" />
            <field column="fileLastModified" name="last_modified" />
            
            <!-- <field column="fileName" name="text" /> -->
            <field column="baseDir" name="text" />

			<entity name="tika-test" 
                 dataSource="bin"  
                 processor="TikaEntityProcessor" 
				 url="${"$"}{sd.fileAbsolutePath}" 
                 format="text" onError="skip" 
                 pk="url" >
				 
				<field column="url" name="id" meta="true" />
                <field column="Author" name="author" meta="true"/>
                <field column="title" name="title" meta="true"/>
				<field column="fileSize" name="size"/> 
				
                <field column="text" name="text"/>
				
				<field column="description" name="description" /> 
				<field column="comments" name="comments" /> 
				<field column="subject" name="subject" /> 
				<field column="links" name="links" /> 
				
				<field column="content_type" name="content_type" /> 
				<field column="last_modified" name="last_modified" /> 
				<field column="created" name="created" /> 
				<field column="category" name="category" /> 
				<field column="keywords" name="keywords" />
				<field column="creator" name="creator" /> 
				<field column="resourceName" name="resourceName" />
				
				<field column="fileName" name="fileName"/>
				<field column="filePath" name="filePath"/>
        </entity>

    </entity>
  </document>

</dataConfig>

