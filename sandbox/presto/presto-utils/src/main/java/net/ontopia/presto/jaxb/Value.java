package net.ontopia.presto.jaxb;

import java.util.Collection;
import java.util.Collections;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class Value {

    private Boolean removable;
    
    // primitive
    private String value;
    
    // reference
    private String id;
    private String name;
    private Collection<Link> links = Collections.emptySet();
    private Topic embedded;
    
    public void setRemovable(Boolean removable) {
        this.removable = removable;
    }
    public Boolean isRemovable() {
        return removable;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setLinks(Collection<Link> links) {
        this.links = links;
    }
    public Collection<Link> getLinks() {
        if (links == null) {
            return Collections.emptySet();
        } else {
            return links;
        }
    }
    public void setEmbedded(Topic embedded) {
        this.embedded = embedded;
    }
    public Topic getEmbedded() {
        return embedded;
    }
    
}
