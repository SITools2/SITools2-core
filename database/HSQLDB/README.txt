1/Create database for SITOOLS2

from the root directory execute: 
java -jar workspace/libraries/org.hsqldb/sqltool.jar --autoCommit --inlineRc=url=jdbc:hsqldb:file:data/HSQLDB/sitools,user=sa,password= < database/HSQLDB/sitools.sql

Database will be created in folder data/HSQLDB/sitools

2/Create database for the SITOOLS2 tests
from the root directory execute: 
java -jar workspace/libraries/org.hsqldb/sqltool.jar --autoCommit --inlineRc=url=jdbc:hsqldb:file:data/TESTS/HSQLDB/sitools,user=sa,password= < database/HSQLDB/sitools-tests.sql

Database will be created in folder data/TESTS/HSQLDB/sitools
