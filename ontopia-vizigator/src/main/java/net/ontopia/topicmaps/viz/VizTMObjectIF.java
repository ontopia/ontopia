
package net.ontopia.topicmaps.viz;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Icon;

import net.ontopia.topicmaps.core.TopicIF;

import com.touchgraph.graphlayout.TGPanel;

/**
 * PRIVATE.
 * INTERNAL.
 */

public interface VizTMObjectIF extends Recoverable {
  TopicIF getTopicMapType();
  void setColor(Color color);
  void setVisible(boolean b);
  void addTo(TGPanel tgpanel);
  void setLineWeight(int lineWeight);
  void setFont(Font font);
  void setIcon(Icon icon);
  void setShape(int shape);
  void deleteFrom(TGPanel tgpanel);
  boolean represents(Object object);
  boolean isEdge();
  boolean isAssociation();
  void setScopingTopic(TopicIF aTopic);
}
