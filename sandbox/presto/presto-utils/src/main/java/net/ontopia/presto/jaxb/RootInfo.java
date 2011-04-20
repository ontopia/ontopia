package net.ontopia.presto.jaxb;

import java.util.Collection;
import java.util.Collections;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class RootInfo {

    private String id;
    private String name;
    private int version;
    
    private Collection<Link> links = Collections.emptySet();

    public RootInfo() {        
    }
    
    public RootInfo(String id, String name) {
        this.id = id;
        this.name = name;
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

    public void setVersion(int version) {
      this.version = version;
    }

    public int getVersion() {
      return version;
    }

}
