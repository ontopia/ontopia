#! /bin/bash

#cd /ontopia/src/java/net/ontopia/topicmaps/impl/rdbms/schema

echo "Generating database scripts..."
mkdir -p setup
java net.ontopia.persistence.rdbms.DDLWriter rdbms-schema.xml generic generic setup/generic.create.sql setup/generic.drop.sql
java net.ontopia.persistence.rdbms.DDLWriter rdbms-schema.xml oracle oracle8,oracle,generic setup/oracle8.create.sql setup/oracle8.drop.sql
java net.ontopia.persistence.rdbms.DDLWriter rdbms-schema.xml oracle oracle9i,oracle,generic setup/oracle9i.create.sql setup/oracle9i.drop.sql
java net.ontopia.persistence.rdbms.DDLWriter rdbms-schema.xml oracle oracle10g,oracle,generic setup/oracle10g.create.sql setup/oracle10g.drop.sql
java net.ontopia.persistence.rdbms.DDLWriter rdbms-schema.xml postgresql postgresql,generic setup/postgresql.create.sql setup/postgresql.drop.sql
java net.ontopia.persistence.rdbms.DDLWriter rdbms-schema.xml sqlserver sqlserver,generic setup/sqlserver.create.sql setup/sqlserver.drop.sql
java net.ontopia.persistence.rdbms.DDLWriter rdbms-schema.xml mysql mysql,generic setup/mysql.create.sql setup/mysql.drop.sql
java net.ontopia.persistence.rdbms.DDLWriter rdbms-schema.xml generic generic setup/h2.create.sql setup/h2.drop.sql

# Following databases not yet 100% supported
#java net.ontopia.persistence.rdbms.DDLWriter rdbms-schema.xml db2 db2,generic setup/db2.create.sql setup/db2.drop.sql
#java net.ontopia.persistence.rdbms.DDLWriter rdbms-schema.xml sapdb sapdb,generic setup/sapdb.create.sql setup/sapdb.drop.sql
#java net.ontopia.persistence.rdbms.DDLWriter rdbms-schema.xml firebird firebird,generic setup/firebird.create.sql setup/firebird.drop.sql
#cd -
