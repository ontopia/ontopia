package net.ontopia.presto.jaxrs;

import java.util.Map;

public interface TopicListener {

    void onTopicUpdated(String topicId);

    Map<String, Object> postProcess(Map<String, Object> topicInfo);
    
}
