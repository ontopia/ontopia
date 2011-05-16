package net.ontopia.topicmaps.impl.tmapi2;

import net.ontopia.infoset.core.LocatorIF;

import org.tmapi.core.Locator;
import org.tmapi.core.TopicMapSystem;

public interface TopicMapSystemIF extends TopicMapSystem {
  public Locator wrapLocator(LocatorIF loc);
  public void remove(LocatorIF loc);
}
