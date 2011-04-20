package net.ontopia.presto.jaxb;

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class TopicTypeTree {

    private String id;
    private String name;
    private Collection<TopicTypeTree> types;
    private Collection<Link> links;

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

    public void setTypes(Collection<TopicTypeTree> types) {
      this.types = types;
    }

    public Collection<TopicTypeTree> getTypes() {
      return types;
    }

    public void setLinks(Collection<Link> links) {
      this.links = links;
    }

    public Collection<Link> getLinks() {
      return links;
    }

}
