 <xsl:stylesheet version="1.0" xmlns="http://www.w3.org/TR/REC-html40"
                               xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="html"/>
   <xsl:output indent="yes"/>
 
   <!-- This is a XSLT stylesheet the generates a HTML file from a Checkstyle XML report. 
         Author: feng.qian@gm... 
   -->
 
   <xsl:template match="/">
     <html>
       <head><title>Checkstyle Results Report</title>
         <style>
         .BlueRim{
           background-color:#3366CC;
         }
         .TableDataRed{
           font-size: .8em;
           text-align: left;
           background: #FFCCCC;
           color: #000000;
         }
         .TableDataYellow{
           font-size: .8em;
           text-align: left;
           background: #FFFFCC;
           color: #000000;
         }
         .TableDataGrey{
           font-size: .8em;
           text-align: left;
           background: #EEEEEE;
           color: #000000;
         }
         .TableHeaderGrey{
           font-size: .8em;
           font-weight: bold;
           text-align: center;
           background: #EEEEEE;
           color: #000000;
           }
         .SummaryTableHeader{
           font-size: .8em;
           font-weight: bold;
           text-align: center;
           background: #EEEEEE;
           color: #000000;
           }
         .SummaryTableDataWhite{
           font-size: .8em;
           background: #FFFFFF;
           color: #000000;
         }
         .SummaryTableDataA {
           font-size: .8em;
           background: #EEEEEE;
           color: #000000;
         }
         .SummaryTableDataB {
           font-size: .8em;
           background: #EFEFEF;
           color: #000000;
         }
         </style>
       </head>
       <body>
         <h1>Checkstyle Results:</h1>
 
         <table border="0" cellspacing="1" width="100%" class="BlueRim">
           <tr class="SummaryTableHeader"><td>File(s)</td><td>Error(s)</td><td>Warning(s)</td><td>Info(s)</td></tr>
           <tr class="SummaryTableDataWhite">
             <td>Total : <xsl:value-of select="count(checkstyle/file)"/></td>
             <td align="center"><xsl:value-of select="count(checkstyle/file/error[@severity='error'])"/></td>
             <td align="center"><xsl:value-of select="count(checkstyle/file/error[@severity='warning'])"/></td>
             <td align="center"><xsl:value-of select="count(checkstyle/file/error[@severity='info'])"/></td>
           </tr>
           <xsl:for-each select="/checkstyle/file">
             <tr><xsl:attribute name="class">
                 <xsl:choose>
                   <xsl:when test="position() mod 2 = 0">
                     SummaryTableDataA
                   </xsl:when>
                   <xsl:when test="position() mod 2 = 1">
                     SummaryTableDataB
                   </xsl:when>
                 </xsl:choose>
               </xsl:attribute>
               <td><a><xsl:attribute name="href">#<xsl:value-of select="position()"/></xsl:attribute><xsl:value-of select="@name"/></a></td> 
               <td align="center"><xsl:value-of select="count(error[@severity='error'])"/></td>
               <td align="center"><xsl:value-of select="count(error[@severity='warning'])"/></td>
               <td align="center"><xsl:value-of select="count(error[@severity='info'])"/></td>
             </tr>
           </xsl:for-each>
         </table>
 
         <h2>Details:</h2>
         <table width="100%">
           <xsl:for-each select="/checkstyle/file">
             <tr><td>
                 <table width="100%">
                   <tr>
                     <td><a><xsl:attribute name="name"><xsl:value-of select="position()"/></xsl:attribute></a><b><xsl:value-of select="@name"/></b></td>
                     <td class="TableDataGrey">info(s): <xsl:value-of select="count(error[@severity='info'])"/></td>
                     <td class="TableDataYellow">warning(s): <xsl:value-of select="count(error[@severity='warning'])"/></td>
                     <td class="TableDataRed">error(s): <xsl:value-of select="count(error[@severity='error'])"/></td>
                   </tr>
                 </table>
             </td></tr>
             <tr><td>
               <table width="100%" border="0" cellspacing="1" class="BlueRim">
                 <tr class="TableHeaderGrey"><td>L</td><td>C</td><td>Severity</td><td>Message</td></tr>
                 <xsl:for-each select="error">
                   <tr><xsl:attribute name="class"> 
                       <xsl:choose>
                         <xsl:when test="@severity='info'">
                           TableDataGrey
                         </xsl:when>
                         <xsl:when test="@severity='warning'">
                           TableDataYellow
                         </xsl:when>
                         <xsl:when test="@severity='error'">
                           TableDataRed
                         </xsl:when>
                       </xsl:choose>
                     </xsl:attribute>
                   <td><xsl:value-of select="@line"/></td>
                   <td><xsl:value-of select="@column"/></td> 
                   <td><xsl:value-of select="@severity"/></td>
                   <td><xsl:value-of select="@message"/></td>
                 </tr>
                 </xsl:for-each>
               </table>
             </td></tr>
           </xsl:for-each>
         </table>
       </body>
     </html>
   </xsl:template>
 </xsl:stylesheet>
 
