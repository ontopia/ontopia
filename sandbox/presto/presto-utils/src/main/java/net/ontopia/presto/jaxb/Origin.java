package net.ontopia.presto.jaxb;

public class Origin {

    private String topicId;
    private String fieldId;

    public Origin() {        
    }
    
    public Origin(String topicId, String fieldId) {
        this.topicId = topicId;
        this.fieldId = fieldId;
    }
    
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
    public String getTopicId() {
        return topicId;
    }
    
    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }
    public String getFieldId() {
        return fieldId;
    }
    
}
