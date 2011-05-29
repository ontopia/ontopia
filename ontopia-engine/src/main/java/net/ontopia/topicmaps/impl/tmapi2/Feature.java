
package net.ontopia.topicmaps.impl.tmapi2;


/**
 * INTERNAL: OKS->TMAPI feature class.
 */

class Feature {

  String name;
  boolean fixed;
  boolean defval;

  Feature(String name, boolean fixed, boolean defval) {
    this.name = name;
    this.fixed = fixed;
    this.defval = defval;
  }
}
