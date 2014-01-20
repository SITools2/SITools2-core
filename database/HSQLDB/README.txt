1/Create database for SITOOLS2

from the root directory execute: 
java -jar workspace/libraries/org.hsqldb/sqltool.jar --autoCommit --inlineRc=url=jdbc:hsqldb:file:data/HSQLDB/sitools,user=sitools,password= < database/HSQLDB/SITOOLS_CREATE_SCHEMA.sql
java -jar workspace/libraries/org.hsqldb/sqltool.jar --autoCommit --inlineRc=url=jdbc:hsqldb:file:data/HSQLDB/sitools,user=sitools,password= < database/HSQLDB/SITOOLS_CREATE_TABLES.sql
java -jar workspace/libraries/org.hsqldb/sqltool.jar --autoCommit --inlineRc=url=jdbc:hsqldb:file:data/HSQLDB/sitools,user=sitools,password= < database/HSQLDB/SITOOLS_INSERT_DATA.sql

Database will be created in folder data/HSQLDB/sitools

Edit the sitools.properties file with the following properties :
Starter.DATABASE_DRIVER=org.hsqldb.jdbcDriver
Starter.DATABASE_URL=jdbc\:hsqldb:file:../../data/HSQLDB/sitools
Starter.DATABASE_USER=sitools
Starter.DATABASE_PASSWORD=sitools
Starter.DATABASE_SCHEMA=sitools


2/Create database for the SITOOLS2 tests
from the root directory execute: 
java -jar workspace/libraries/org.hsqldb/sqltool.jar --autoCommit --inlineRc=url=jdbc:hsqldb:file:data/TESTS/HSQLDB/sitools,user=sitools,password= < database/HSQLDB/sitools-tests.sql

Database will be created in folder data/TESTS/HSQLDB/sitools
