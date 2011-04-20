package net.ontopia.presto.jaxb;

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class AvailableFieldTypes {

    private String id;
    private String name;
    private Collection<TopicType> types;

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

    public void setTypes(Collection<TopicType> types) {
      this.types = types;
    }

    public Collection<TopicType> getTypes() {
      return types;
    }

}
