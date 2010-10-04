
package ontopoly.model;

/**
 * Represents an interface control type which can be assigned to an
 * association field.
 */
public interface InterfaceControlIF extends OntopolyTopicIF {

  /**
   * Tests whether this interface control is on:drop-down-list.
   * 
   * @return true if this interface control is on:drop-down-list.
   */
  public boolean isDropDownList();

  /**
   * Tests whether this interface control is on:search-dialog.
   * 
   * @return true if this interface control is on:search-dialog.
   */
  public boolean isSearchDialog();

  /**
   * Tests whether this interface control is on:browse-dialog.
   * 
   * @return true if this interface control is on:browse-dialog.
   */
  public boolean isBrowseDialog();

  /**
   * Tests whether this interface control is on:auto-complete
   * 
   * @return true if this interface control is on:auto-complete.
   */
  public boolean isAutoComplete();

}
