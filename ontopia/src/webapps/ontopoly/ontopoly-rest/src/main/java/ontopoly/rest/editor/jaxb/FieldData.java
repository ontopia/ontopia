package ontopoly.rest.editor.jaxb;

import java.util.Collection;
import java.util.Collections;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class FieldData {

    private String id;
    private String name;
    private Boolean readOnly;
    private String datatype;
    private String validation;
    private String interfaceControl;
    
    private Integer minCardinality;
    private Integer maxCardinality;
    
    private Collection<Link> links = Collections.emptySet();
    private Collection<Value> values;
    
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

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Boolean isReadOnly() {
        return readOnly;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getValidation() {
        return validation;
    }

    public void setInterfaceControl(String interfaceControl) {
        this.interfaceControl = interfaceControl;
    }

    public String getInterfaceControl() {
        return interfaceControl;
    }

    public void setMinCardinality(Integer minCardinality) {
        this.minCardinality = minCardinality;
    }

    public Integer getMinCardinality() {
        return minCardinality;
    }

    public void setMaxCardinality(Integer maxCardinality) {
        this.maxCardinality = maxCardinality;
    }

    public Integer getMaxCardinality() {
        return maxCardinality;
    }

    public void setValues(Collection<Value> values) {
        this.values = values;
    }

    public Collection<Value> getValues() {
        return values;
    }

}
