
// $Id: AbstractCoreTestGenerator.java,v 1.10 2006/03/22 09:45:52 grove Exp $

package net.ontopia.topicmaps.core;

import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

  public interface TestFactoryIF {

    public TopicMapStoreIF makeStandaloneTopicMapStore();
    
    public TopicMapReferenceIF makeTopicMapReference();

    public void releaseTopicMapReference(TopicMapReferenceIF topicmapRef);

  }
