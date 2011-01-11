
package net.ontopia.topicmaps.utils.sdshare.client;

import java.util.List;
import java.util.ArrayList;

/**
 * PUBLIC: Represents a FragmentFeed as read from an SDshare server.
 */
public class FragmentFeed {
  private String prefix;
  private List<Fragment> fragments;

  public FragmentFeed() {
    this.fragments = new ArrayList<Fragment>();
  }
  
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getPrefix() {
    return prefix;
  }

  public void addFragment(Fragment fragment) {
    fragments.add(fragment);
    fragment.setParent(this);
  }

  public List<Fragment> getFragments() {
    return fragments;
  }
}
