package net.ontopia.presto.spi.impl.ontopoly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ontopoly.model.FieldsView;
import net.ontopia.presto.spi.PrestoSchemaProvider;
import net.ontopia.presto.spi.PrestoView;

public class OntopolyView implements PrestoView {

  private final OntopolySession session;
  private final FieldsView fieldsView;

  OntopolyView(OntopolySession session, FieldsView fieldsView) {
    this.session = session;
    this.fieldsView = fieldsView;    
  }
  
  static FieldsView getWrapped(PrestoView view) {
    return ((OntopolyView)view).fieldsView;
  }

  public String getId() {
    return session.getStableId(fieldsView);
  }

  public PrestoSchemaProvider getSchemaProvider() {
    return session.getSchemaProvider();
  }
  
  public String getName() {
    return fieldsView.getName();
  }

  static Collection<PrestoView> wrap(OntopolySession session, Collection<FieldsView> fieldViews) {
    List<PrestoView> result = new ArrayList<PrestoView>(fieldViews.size());
    for (FieldsView fieldView : fieldViews) {
      result.add(new OntopolyView(session, fieldView));
    }
    return result;
  }
 
}
