<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="output" select="'man'" />
<xsl:param name="ROOT_DIRECTORY" select="'D:/CNES_ULISSE'" />
<xsl:param name="LOG_DIRECTORY" select="'${HOME}'" />
<xsl:param name="PORT" select="'8182'" />
	<xsl:template match='/' >
	<xsl:if test="$output = 'man'">
	<xsl:message>Parameters :</xsl:message>
	<xsl:message>param output values : man, ant, bat, shell, eclipse</xsl:message>
	<xsl:message>param ROOT_DIRECTORY default value D:/CNES_ULISSE </xsl:message>
	<xsl:message>param LOG_DIRECTORY default value ${HOME} </xsl:message>
	<xsl:message>param PORT default value 8182 </xsl:message>
	</xsl:if>
	<xsl:if test="$output !=  'man'">
	<xsl:message><xsl:value-of select="$ROOT_DIRECTORY" /></xsl:message>
	<xsl:apply-templates/>
	</xsl:if>
	</xsl:template>
	
	<xsl:template match="/eclipse-userlibraries">
	<xsl:if test="$output='ant'" >
		<xsl:message> Sortie vers fichier ant de classpath </xsl:message>
		<xsl:call-template name="output-ant" />
	</xsl:if>
	<xsl:if test="$output ='bat'" >
		<xsl:message> Sortie vers fichier script windows </xsl:message>
		<xsl:call-template name="output-bat" />
	</xsl:if>
	<xsl:if test="$output ='shell'" >
		<xsl:message> Sortie vers fichier script shell </xsl:message>
		<xsl:call-template name="output-shell" />
	</xsl:if>
	<xsl:if test="$output ='eclipse'" >
		<xsl:message> Sortie vers fichier eclipse local </xsl:message>
		<xsl:call-template name="output-eclipse" />
	</xsl:if>
	</xsl:template>

	<!-- 
	
	Sortie xml ant des classpaths
	
	 -->
	<xsl:template name="output-ant" >
	<project name="sitools-userlibraries">
		<xsl:for-each select="library">
		<path>
			<xsl:attribute name="id"><xsl:value-of select="./@name"/></xsl:attribute>
			<filelist>
			<xsl:for-each select="./archive">
				<file>
				<xsl:attribute name="name"><xsl:value-of select="replace(@path,'D:/CNES-ULISSE', $ROOT_DIRECTORY)"/></xsl:attribute>
				</file>
			</xsl:for-each>
			</filelist>
		</path> 
		</xsl:for-each>
	</project>
	</xsl:template>
	
	<!-- 
	
	Sortie xml ant des classpaths
	
	 -->
	<xsl:template name="output-eclipse" >
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="archive">
		<archive>
		<xsl:attribute name="path" select="replace(@path,'D:/CNES-ULISSE', $ROOT_DIRECTORY)"/>
		</archive>
	</xsl:template>
	
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>	
	<!-- 
	
	Sortie script shell de lancement
	
	 -->
	<xsl:template name="output-shell" >#!/bin/bash
# ---------
# Fonctions
# ---------

checkIfRunning() {
    local pid=`cat ${LOG_DIR}/${prog}.run`
    [ "`ps -ef |grep ${pid} | grep -v 'grep' | awk '{print $2}'`" != "" ] &#38;&#38; return 1
    return 0
       
}

# ---------
# principal
# ---------

# Chemins courants du sitools
sitoolsHome="<xsl:value-of select="$ROOT_DIRECTORY"/>"
sitoolsSnap="${sitoolsHome}/workspace"
sitoolsCots="${sitoolsHome}/cots"
sitoolsCore="${sitoolsSnap}/fr.cnes.sitools.core"

# Parametres du script
prog=`basename ${0}`
myDir=`dirname ${0}`
myPid=${$}

sitoolsCore="${sitoolsSnap}/fr.cnes.sitools.core"

sitoolsJarName="<xsl:value-of select="$PORT"/>_fr.cnes.sitools.core.jar"


# Creation du repertoire et du fichier 'LOG'
LOG_DIR="<xsl:value-of select="$LOG_DIRECTORY"/>/LOG"
[ ! -d ${LOG_DIR} ] &#38;&#38; mkdir -p ${LOG_DIR}
LOG="${LOG_DIR}/${prog}-${myPid}.log"

SITOOLS_PROPS="${sitoolsCore}/sitools.properties"
if [ ! -f ${SITOOLS_PROPS} ];then
    echo "--- ERREUR ---" | tee -a ${LOG}
    echo "Impossible de trouver ${SITOOLS_PROPS}. Abandon." | tee -a ${LOG}
    echo "--- ERREUR ---" | tee -a ${LOG}
    exit 1
fi

# Lancement de JAVA
if [ -f ${LOG_DIR}/${prog}.run ];then
    checkIfRunning
    if [ ${?} -ne 0 ];then
        echo "sitools est deja lance." | tee -a ${LOG}
        exit 0
    fi
    \rm ${LOG_DIR}/${prog}.run
fi

ARGS="-Xms256m -Xmx512m -Djava.net.preferIPv4Stack=true -Djava.awt.headless=true -Djava.util.logging.config.file=${sitoolsCore}/conf/properties/sitools-logging.properties"



if [ "$i" != "--tests" ];then
 #List of parameters to pass to the java program
 PROGRAM_PARAMS="${1}"
 echo "Refreshing ClassPath for plugins ..."
 nohup java -jar ${sitoolsSnap}/sitools-update-classpath/sitools-update-classpath.jar --tmp_directory=./ext --directory=./ext --jar_target=./${sitoolsJarName} 2>&#38;1 | tee -a ${LOG}
 echo "Lancement de JAVA sitools..." | tee -a ${LOG}
 nohup java -jar ${ARGS} ${sitoolsCore}/${sitoolsJarName} ${PROGRAM_PARAMS} 2>&#38;1 | tee -a ${LOG} &#38;
else
 echo "Lancement de la suite de tests JAVA sitools..." | tee -a ${LOG}
 nohup java -jar ${ARGS} ${sitoolsCore}/fr.cnes.sitools.test.jar 2>&#38;1 | tee -a ${LOG} &#38;
fi

sitoolsPid=`ps -ef |grep $sitoolsJarName | grep -v 'grep' | awk '{print $2}'`
echo "Ecriture du fichier PID [${sitoolsPid}]"
echo "${sitoolsPid}" > ${LOG_DIR}/${prog}.run
# -------------
# fin du script
# -------------
</xsl:template>
<!-- Template to replace "/" by "\" for windows-->
<xsl:template name="rightSlashes">
	<xsl:param name="string" />
	<xsl:if test="contains($string, '/')">
		<xsl:value-of select="substring-before($string, '/')" />\<xsl:call-template name="rightSlashes">
                <xsl:with-param name="string">
                <xsl:value-of select="substring-after($string, '/')" />
                </xsl:with-param>
        </xsl:call-template>
	</xsl:if>
	<xsl:if test="not(contains($string, '/'))">
		<xsl:value-of select="$string" />
	</xsl:if>
</xsl:template>
<!-- Sortie bat windows -->
<xsl:template name="output-bat" >
<xsl:variable name="WIN_ROOT">
	<xsl:call-template name="rightSlashes">
		<xsl:with-param name="string">
			<xsl:value-of select="$ROOT_DIRECTORY"/>
		</xsl:with-param>
	</xsl:call-template>
</xsl:variable>
<xsl:variable name="WIN_LOG"><xsl:value-of select="'%USERPROFILE%'"/>
	<xsl:if test="$LOG_DIRECTORY='${HOME})'">
		<xsl:call-template name="rightSlashes">
			<xsl:with-param name="string">
			<xsl:value-of select="$LOG_DIRECTORY"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:if>
</xsl:variable>
@ECHO off
:: Batch file to start Sitools2
:: written by m.marseille (AKKA) 20/01/2011

:: Clear the screen
CLS
SETLOCAL
TITLE startSitools

:: Chemins courants du sitools
SET sitoolsHome="<xsl:value-of select="$WIN_ROOT"/>"
SET sitoolsSnap=%sitoolsHome%\workspace
SET sitoolsCots=%sitoolsHome%\cots
SET sitoolsCore=%sitoolsSnap%\fr.cnes.sitools.core

:: Parametres du script
SET prog=%0
SET prog=%prog:.bat=%
SET myDir=CHDIR
FOR /F "tokens=2 delims= " %%A IN ('TASKLIST /FI ^"WINDOWTITLE eq startSitools^" /NH') DO SET myPid=%%A

:: Creation du repertoire et du fichier 'LOG'
SET LOG_DIR="<xsl:value-of select="$WIN_LOG"/>\LOG"
IF NOT EXIST %LOG_DIR% MKDIR %LOG_DIR%
SET LOG="%LOG_DIR:~1,-1%\%prog%-%myPid%.log"
IF EXIST %LOG% DEL %LOG%
ECHO Fichier de LOG : %LOG:~1,-1%

:: Verifie que le fichier sitools.properties est présent
SET SITOOLS_PROPS=%sitoolsCore%\sitools.properties
IF EXIST %SITOOLS_PROPS% GOTO NOERROR 
ECHO --- ERREUR --- > %LOG%
ECHO Impossible de trouver %SITOOLS_PROPS%. Abandon. >> %LOG%
ECHO --- ERREUR --- >> %LOG%
GOTO :EOF
:NOERROR

:: Lancement de JAVA
SET ARGS=-Xms256m -Xmx512m -Djava.net.preferIPv4Stack=true -Djava.util.logging.config.file=%sitoolsCore%/conf/properties/sitools-logging.properties -Dfile.encoding=utf-8
IF "%1"=="--tests" GOTO tests
	::List of parameters to pass to the java program
	SET PROGRAM_PARAMS=%1
  	TITLE Sitools2
  	ECHO Refreshing CLASSPATH
	java -jar %sitoolsSnap%/sitools-update-classpath/sitools-update-classpath.jar --tmp_directory=ext --directory=ext --jar_target=fr.cnes.sitools.core.jar 2>&amp;1 >> %LOG%
  	ECHO JAVA Sitools2 starting ...
  	ECHO JAVA Sitools2 starting ... >> %LOG%
  	java -jar %ARGS% fr.cnes.sitools.core.jar %PROGRAM_PARAMS% >> %LOG% 2>&amp;1
  	GOTO :EOF
:tests
	TITLE Sitools2-Tests
	ECHO JAVA Sitools2 test suite starting ...
	ECHO JAVA Sitools2 test suite starting ... >> %LOG%
	java -jar %ARGS% fr.cnes.sitools.test.jar


:: -------------
:: fin du script
:: -------------

ENDLOCAL		

</xsl:template>

</xsl:stylesheet>