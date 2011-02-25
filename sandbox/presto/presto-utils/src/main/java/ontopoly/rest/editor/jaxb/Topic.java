package ontopoly.rest.editor.jaxb;

import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Topic {

    private String id;
    private String name;
    private Boolean readOnlyMode;

    private TopicType type;
    private String view;
    
    private Collection<Link> links = Collections.emptySet();

    private Collection<FieldData> fields;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setReadOnlyMode(Boolean readOnlyMode) {
        this.readOnlyMode = readOnlyMode;
    }

    public Boolean isReadOnlyMode() {
        return readOnlyMode;
    }

    public void setType(TopicType type) {
        this.type = type;
    }

    public TopicType getType() {
        return type;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public void setFields(Collection<FieldData> fields) {
        this.fields = fields;
    }

    public Collection<FieldData> getFields() {
        return fields;
    }
    
    // builder methods
    
    public static Topic topic(String id, String name) {
        Topic result = new Topic();
        result.setId(id);
        result.setName(name);
        return result;
    }
    
}
