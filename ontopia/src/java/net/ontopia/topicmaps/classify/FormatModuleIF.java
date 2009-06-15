
// $Id: FormatModuleIF.java,v 1.7 2007/05/07 08:11:39 geir.gronmo Exp $

package net.ontopia.topicmaps.classify;

import java.io.*;

import net.ontopia.utils.*;

/**
 * INTERNAL: Interface that encapsulates the support for a given
 * document format.
 */
public interface FormatModuleIF {

  /**
   * INTERNAL: Returns true if the content of the classifiable content
   * is considered to be of the supported format.
   */
  public boolean matchesContent(ClassifiableContentIF cc);

  /**
   * INTERNAL: Returns true if the identifier of the classifiable
   * content is considered to be indicating the supported format.
   */
  public boolean matchesIdentifier(ClassifiableContentIF cc);

  /**
   * INTERNAL: Reads and analyzes the classifiable content and
   * triggers callbacks on the text handler to identify the text and
   * the structure of the classifiable content.
   */
  public void readContent(ClassifiableContentIF cc, TextHandlerIF handler);
  
}
