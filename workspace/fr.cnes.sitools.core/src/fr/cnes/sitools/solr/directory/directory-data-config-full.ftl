<dataConfig>
    <dataSource type="BinFileDataSource" name="bin" />
    <document name="${document}">
      <entity name="sd"
        processor="FileListEntityProcessor"
        fileName="${fileName}"
        baseDir="${baseDir}"
        recursive="true"
        rootEntity="false"
		onError="skip"

		<#if newerThan??> newerThan="${newerThan}"</#if>
        <#if olderThan??> olderThan="${olderThan}"</#if>
		<#if biggerThan??> biggerThan=${biggerThan}</#if>
		<#if smallerThan??> smallerThan="${smallerThan}"</#if>
          >
            <field column="fileAbsolutePath" name="path" />
            <field column="fileSize" name="size" />
            <field column="fileLastModified" name="lastmodified" />
            
            <!-- <field column="fileName" name="text" /> -->
            <field column="baseDir" name="text" />

			<entity name="tika-test" 
                 dataSource="bin"  
                 processor="TikaEntityProcessor" 
				 url="${"$"}{sd.fileAbsolutePath}" 
                 format="text" onError="skip" >
				 
				<field column="uid" name="url" meta="true" />
                <field column="Author" name="author" meta="true"/>
                <field column="title" name="title" meta="true"/>
                <field column="text" name="text"/>
				
				<field column="description" name="description" /> 
				<field column="comments" name="comments" /> 
				
				<field column="content_type" name="content_type" /> 
				<field column="last_modified" name="last_modified" /> 
				<field column="fileSize" name="size"/> 
                
                <field column="text" name="text"/>
				
				<field column="fileName" name="fileName"/>
        </entity>

    </entity>
  </document>

</dataConfig>


