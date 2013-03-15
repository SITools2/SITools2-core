SITools2 README file

Directories
	cots : project libraries
	documentation : project documentation
	database : database scripts
	workspace : source code
		- client-admin : client for administrators
		- client-user : client for users
		- client-public : client common
		- sitools-izpack-install : installer
		- fr.cnes.sitools.core : server core
		- fr.cnes.sitools.extensions : server extensions
		- fr.cnes.sitools.ext.test : server extensions for tests
		- org.restlet.ext.solr : Solr 3.1 Restlet extension
		- org.restlet.security : Restlet security extensions
		- sitools-build : Can be used to change the ROOT_DIRECTORY property in various build.properties files
		- sitools-tests-testng : selenium tests project
	data : data directories

HOW TO :	
	
HOW TO COMPILE SITOOLS
1 - in the core project, in the folder conf/build/properties create a file named build-<your_name>.properties 
2 - fill in the properties ( copy them from another build-<name>.properties )
3 - in the core project, in the build.properties file ( root folder ) change the HOST value. Set it to <your_name> ( HOST = <your_name>)
4 - run the default ant task from the build.xml file ( root folder )

HOW TO RUN SITOOLS
1 - in the core project, run the script : ./sitools start

HOW TO EXECUTE THE TESTS
1 - Compile the core
2 - Set up the database as described in the "DG-ULISSE.doc" document
3 - in the core project, run the "execute-tests" ant task from the build.xml file ( root folder )

HOW TO COMPILE THE EXTENSIONS
1 - Compile the core
2 - in the extensions project, run the default ant task from the build.xml file ( root folder )

HOW TO COMPILE THE IZPACK INSTALLER 
1 - Compile the core and the extensions
2 - In the installer project, in the build.properties ( root folder ) change the ROOT_DIRECTORY_LOCAL value. Set it to the folder containing "workspace"
3 - run the default ant task from the build.xml file ( root folder )

For more information please consult the documentation directory