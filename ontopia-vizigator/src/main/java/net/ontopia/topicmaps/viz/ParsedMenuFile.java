
package net.ontopia.topicmaps.viz;

import java.util.Map;

public class ParsedMenuFile {
  protected Map enabledItemIds;

  public ParsedMenuFile(Map enabledItemIds) {
    this.enabledItemIds = enabledItemIds;
  }

  public boolean enabled(String itemId) {
    if (enabledItemIds == null)
      return true;
    
    if (enabledItemIds.containsKey(itemId))
      return ((Boolean)enabledItemIds.get(itemId)).booleanValue();
    else
      return !itemId.equals("copy.name");
  }
}
