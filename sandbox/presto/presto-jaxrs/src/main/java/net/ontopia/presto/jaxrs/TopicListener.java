package net.ontopia.presto.jaxrs;

public interface TopicListener {
    void onTopicUpdated(String topicId);
}
