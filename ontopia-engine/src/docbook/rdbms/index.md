The RDBMS Backend Connector
===========================

### Introduction ###

Besides the storage to several file formats, Ontopia provides a storage for Topic Maps in a 
relational database (RDBMS). Storing Topic Maps in a database will allow you to use Topic maps in a
multi user, multi threaded, transactional environment.

#### Supported RDBMS ####

Ontopia has been tested with the following RDBMS:

* [Oracle](https://www.oracle.com/database) (version 8.x and up)
* [PostgreSQL](http://www.postgresql.org/) (7.x and up)
* [MySQL](https://www.mysql.com/)
* [H2](http://www.h2database.com/)
* [Microsoft SQL Server](https://www.microsoft.com/en-us/server-cloud/products/sql-server/)
* Many others via generic SQL 

IF your RDBMS of choice is not on this list, it might still be supported via the generic SQL adapter,
but it will be untested. You can ask for experience or support regarding your RDBMS on the 
[Ontopia mailing list](https://groups.google.com/forum/#!forum/ontopia).

### Guides ###

 * [Installation Guide](install.html)  
   This document provides information regarding the requirements and installation procedure for using
   the RDBMS bakcend connector.

 * [Developer Guide](devguide.html)  
   This document provides information regarding the programmatical use of the RDBMS connector.

<notoc/>
