package net.ontopia.topicmaps.impl.utils;

import net.ontopia.topicmaps.impl.utils.IndexManagerIF;

public abstract class AbstractIndexManager implements IndexManagerIF {

  /**
   * INTERNAL: Register the specified index with the index manager.
   * @param name The to register the index with name.
   * @param index The index to register.
   */
  public abstract void registerIndex(String name, AbstractIndex index);
  
}
