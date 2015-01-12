![](workspace/client-public/res/images/logo_01_petiteTaille.png)
# SITools2
## Description
SITools2 is a new CNES generic tool performed by a joint effort between CNES and scientific laboratories. The aim of SITools is to provide a self-manageable data access layer deployed on already existing scientific laboratory databases.

For more information : [http://sourceforge.net/projects/sitools2/](http://sourceforge.net/projects/sitools2/ "SITools2 Web Site")

Release notes : [README.txt](workspace/sitools-build/files/README.txt)

## Building SITools2

### Getting the sources

	$ git clone https://github.com/SITools2/core-v2.git sitools2-v2
	
### Pre-build configuration

	$ cd sitools2-v2/workspace/fr.cnes.sitools.core

Edit `build.properties` and change the value of the `HOST` property (for example HOST = new-dev)

Make a copy of `conf/build/properties/build-example.properties` to `conf/build/properties/build-new-dev.properties`

	$ cp conf/build/properties/build-example.properties conf/build/properties/build-new-dev.properties


Edit the newly created file and set the `ROOT_DIRECTORY` property to the sitools2-v2 folder

### Build the sources

Build the sources using ant

	$ ant

## Building the installer from the sources
### Build the core

Build the sitools core using the instructions above

### Build the extensions

	$ cd sitools2-v2/workspace/fr.cnes.sitools.extensions
	$ ant

### Build the installer

	$ cd sitools2-v2/workspace/sitools-install-izpack	

Edit the `build.properties` file and update the `ROOT_DIRECTORY_LOCAL` with the value of the `sitools2-v2` folder path.

Build the installer using ant

	$ ant

## Installing from the installer
	$ java -jar SITools2-<version>-install.jar

## Starting SITools2

	$ sh sitools.sh start
or 

	$ ./sitools2-v2/workspace/fr.cnes.sitools.core/sitools start
if `source` is not installed on your computer.

## First step in SITools2

### Administration UI

Go to : http://{publicHostDomain}/sitools/client-admin/index.html

- user : **admin** 
- password : **admin**

### Portal UI

Go to : http://{publicHostDomain}/sitools/client-portal/index.html

## Features
- Client/server application
- REST architecture
- User management
- Data sources management
- Datasets management (SQL builder as WYSIWYG Editor, ...)
- Projects management
- Security management
- Conversions capabilities such as units, ...
- Query form builder as WYSIWYG editor
- Extensible Framework (plug-ins, ...) at the server level
- Application module management on the client side
- Internationalization
- RSS management
- Open search management
