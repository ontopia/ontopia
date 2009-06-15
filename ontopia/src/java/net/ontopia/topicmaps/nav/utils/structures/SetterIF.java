// $Id: SetterIF.java,v 1.8 2004/11/12 11:47:19 grove Exp $

package net.ontopia.topicmaps.nav.utils.structures;

/** 
 * INTERNAL: An interface which allows child tags to send an object to their parent.
 * <p>Examples:
 * <ul>
 * <li>DisplayTag passes DisplayIF to ClusterTag 
 * <li>ClusterTag passes ClusterIF to parent topicmap tag
 * <li>BlockTag passes Block to parent topicmap tag
 * </ul>
 */
public interface SetterIF {

  public void set(String name, Object o);

}





