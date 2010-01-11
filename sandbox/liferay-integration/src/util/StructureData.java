package util;

import com.liferay.portlet.journal.model.JournalStructure;

/**
 * This class provides all data required by ontopia to create topics for custom structures from liferay. This is the place where extensions of these
 * data should go, as well where changes in the way the data are presented are to be made.
 */
public class StructureData implements UuidIdentifiableIF{

  private String _uuid;
  private String _parentId;
  private String _structureId;
  private String _name;
  
  private StructureData(){
    super();
  }
  
  public StructureData(JournalStructure structure){
    setName(structure.getName());
    setParentId(structure.getParentStructureId());
    setStructureId(structure.getStructureId());
    setUuid(structure.getUuid());
  }
  public void setUuid(String uuid) {
    _uuid = uuid;
  }
  public String getUuid() {
    return _uuid;
  }
  public void setParentId(String parentId) {
    _parentId = parentId;
  }
  public String getParentId() {
    return _parentId;
  }
  public void setStructureId(String structureId) {
    _structureId = structureId;
  }
  public String getStructureId() {
    return _structureId;
  }
  public void setName(String name) {
    _name = name;
  }
  public String getName() {
    return _name;
  }
}
