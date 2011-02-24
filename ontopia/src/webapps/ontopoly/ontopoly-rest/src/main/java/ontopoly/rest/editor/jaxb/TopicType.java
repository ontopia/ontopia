package ontopoly.rest.editor.jaxb;

import java.util.Collection;
import java.util.Collections;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class TopicType {

    private String id;
    private String name;
    private Boolean readOnly;

    private Collection<Link> links = Collections.emptySet();

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

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Boolean isReadOnly() {
        return readOnly;
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

}
