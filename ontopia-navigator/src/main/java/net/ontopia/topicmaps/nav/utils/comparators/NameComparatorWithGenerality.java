
package net.ontopia.topicmaps.nav.utils.comparators;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.topicmaps.utils.VariantNameGrabber;
import net.ontopia.infoset.impl.basic.URILocator;

/**
 * INTERNAL: A Comparator for ordering TopicNameIFs and VariantNameIFs
 * first after their generality (determined by the number of themes in
 * their scopes, which means that the name in the unconstrained scope
 * would always appear first) and second alphabetically
 * (case-independent).
 */
public class NameComparatorWithGenerality extends NameComparator
  implements Comparator {

  /**
   * Empty constructor, used on application startup to initialise a
   * "fast" comparator which will compare TopicNameIFs and
   * VariantNameIFs using no context.
   */
  public NameComparatorWithGenerality() {
    super();
  }

  /**
   * Constructor used to make a comparator which will compare
   * TopicNameIFs and VariantNameIFs using the context provided.
   */
  public NameComparatorWithGenerality(Collection context) {
    super(context);
  }

  /**
   * INTERNAL: helper method which gets out of the object the
   * base name or variant name value. If it's a basename try to
   * retrieve the sort variant name for it. The resulting string
   * contains first the number of themes and after that the name.
   */
  protected String getName(Object obj) throws ClassCastException {
    String value = null;
    
    // --- first try if it's a base name
    try {
      TopicNameIF basename = (TopicNameIF) obj;

      // try to get sort variant name for this base name
      initSortNameGrabber( basename );
      VariantNameIF sortVariant = (VariantNameIF) sortNameGrabber.grab( basename );
      if (sortVariant != null) {
        if (sortVariant.getValue() != null)
          value = sortVariant.getValue();
        else
          value = sortVariant.getLocator().getAddress();
      }
      else
        value = basename.getValue();
      // order in first instance after the generality
      value = basename.getScope().size() + value;

    } catch (ClassCastException e) {
      // --- ...second try if it's a variant name
      VariantNameIF variant = (VariantNameIF) obj;
      if (variant.getValue() != null)
        value = variant.getValue();
      else
        value = variant.getLocator().getAddress();
      // order in first instance after the generality
      value = variant.getScope().size() + value;
    }
    
    return value;
  }
  
}





