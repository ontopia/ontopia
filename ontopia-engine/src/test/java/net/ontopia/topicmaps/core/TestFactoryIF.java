
package net.ontopia.topicmaps.core;

import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

  public interface TestFactoryIF {

    public TopicMapStoreIF makeStandaloneTopicMapStore();
    
    public TopicMapReferenceIF makeTopicMapReference();

    public void releaseTopicMapReference(TopicMapReferenceIF topicmapRef);

  }
