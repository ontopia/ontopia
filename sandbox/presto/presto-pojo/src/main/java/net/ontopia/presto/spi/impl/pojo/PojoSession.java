package net.ontopia.presto.spi.impl.pojo;

import net.ontopia.presto.spi.PrestoDataProvider;
import net.ontopia.presto.spi.PrestoSchemaProvider;
import net.ontopia.presto.spi.PrestoSession;

public class PojoSession implements PrestoSession {

    private String databaseId;
    private String databaseName;
    
    private PrestoSchemaProvider schemaProvider;
    private PrestoDataProvider dataProvider;

    public PojoSession(String databaseId, String databaseName, PrestoSchemaProvider schemaProvider, PrestoDataProvider dataProvider) {
        this.databaseId = databaseId;
        this.databaseName = databaseName;
        this.schemaProvider = schemaProvider;
        this.dataProvider = dataProvider;        
    }
    
    public String getDatabaseId() {
        return databaseId;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void abort() {
    }

    public void commit() {
    }

    public void close() {
    }

    public PrestoDataProvider getDataProvider() {
        return dataProvider;
    }

    public PrestoSchemaProvider getSchemaProvider() {
        return schemaProvider;
    }

}
