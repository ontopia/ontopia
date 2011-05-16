
// $Id: Constants.java,v 1.9 2007/11/06 14:25:08 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.basic;


/**
 * INTERNAL: General (non-action specific) Constants which are used
 * through the complete Web Editor Framework.
 */
public class Constants {

  // ----------------------------------------------------------------------
  // Request attribute and parameter names
  // ----------------------------------------------------------------------
  
  // application attributes
  public static final String AA_REGISTRY       = "actionRegistry";
  public static final String AA_SCHEMAS        = "schemaRegistry";

  // servlet context init parameters
  public static final String SCTXT_CONFIG_PATH     = "action_config";
  public static final String SCTXT_SCHEMAS_ROOTDIR = "schemas_rootdir";
  public static final String SCTXT_VELOCITY_ENGINE  = "velocity_engine";
  public static final String SCTXT_VELOPROPS_PATH  = "velocity_properties";
  public static final String SCTXT_RELOAD_DELAY    = "config_reload_delay";

  // servlet config init parameters
  public static final String SCNFG_DEBUG       = "debug";
  
  // request parameter names 
  public static final String RP_TOPICMAP_ID    = "tm";
  public static final String RP_TOPIC_ID       = "id";
  public static final String RP_ASSOC_ID       = "assoc_id";
  public static final String RP_ASSOC_TYPE_ID  = "assoc_type_id";
  public static final String RP_ROLE_TYPE_ID   = "role_type_id";
  public static final String RP_OCC_TYPE_ID    = "occ_type_id";
  public static final String RP_ACTIONGROUP    = "ag";
  public static final String RP_LOCKVAR        = "lock_var";
  public static final String RP_SHOW_AG        = "show_ag";
  public static final String RP_SEL_TOPIC_ID   = "selected_topic_id";
  public static final String RP_TOPIC_TYPE_ID  = "topic_type_id";
  public static final String RP_BN_THEME_ID    = "bn_theme_id";
  public static final String RP_NEXTACTION     = "nextaction";
  public static final String RP_SEARCHCON      = "searchContext";
  public static final String RP_NAV_MODE       = "navMode";
  public static final String RP_SEARCH         = "search";
  public static final String RP_SEARCHFORM     = "disp_search_form";
  public static final String RP_LOOKUP_STR     = "lookup_for";
  public static final String RP_LOOKUP_METHOD  = "lookup_method";
  public static final String RP_LEAVE          = "leave";
  public static final String RP_ERR_MESSAGE    = "errMsg";
  public static final String RP_REQUEST_ID     = "requestid";
  public static final String RP_OPERATION      = "operation";
  public static final String RPVAL_UNLOAD      = "unload";
  public static final Object RPVAL_UNLOCK      = "unlock";

  public static final String RP_REL_URL        = "relativeURL";
  public static final String RP_FRAMENAME      = "framename";
  public static final String RP_ERR_LIST       = "errorList";

  public static final String[] OBJ_REQPARAMS = {
    RP_TOPICMAP_ID,
    RP_TOPIC_ID,
    RP_ASSOC_ID,
    RP_ACTIONGROUP
  };
    
  // request attribute names
  public static final String RA_TOPICMAP    = "topicmap";
  public static final String RA_SCHEMA      = "schema";
  public static final String RA_ACTIONGROUP = "action_group";

  // form names
  public static final String FORM_EDIT_NAME = "tmEditForm";  
  public static final String OKS_FORM_READONLY = "OKS_FORM_READONLY";  
  public static final String LOCK_RESULT = "LOCK_RESULT";

  // The central controller: Process Servlet handles modification requests.
  // Has to be concatenated with the context, for example '/omnieditor/process',
  // see the web application configuration in 'WEB-INF/web.xml'.
  public static final String PROCESS_SERVLET = "process";

  // used for types of forward pages
  public static final int FORWARD_SUCCESS = 1; // 01
  public static final int FORWARD_FAILURE = 2; // 10
  public static final int FORWARD_GENERIC = 3; // 11

  // used as default values
  public static final String DUMMY_LOCATOR = "http://www.example.com";
  
  // ----------------------------------------------------------------------
  // OmniEditor specific constants
  // ----------------------------------------------------------------------
  
  // action groups
  public static final String AG_TOPIC_EDIT_PROPS   = "topicEditProps";
  public static final String AG_TOPIC_EDIT_NAMES   = "topicEditNames";
  public static final String AG_TOPIC_EDIT_INTOCCS = "topicEditIntOccs";
  public static final String AG_TOPIC_EDIT_EXTOCCS = "topicEditExtOccs";
  public static final String AG_TOPIC_EDIT_ASSOCS  = "topicEditAssocs";
  public static final String AG_ASSOC_EDIT         = "assocEdit";
  public static final String AG_TM_EDIT            = "tmEdit";

  // JSP names (see also process_helper.jsp for the URL handling)
  public static final String JSP_START_EDIT      = "start_edit.jsp";
  public static final String JSP_START_HELP      = "start_help.jsp";
  public static final String JSP_NAVIGATION      = "navigation.jsp";
  public static final String JSP_TOPICMAP_EDIT   = "topicmap_edit.jsp";
  public static final String JSP_TOPICMAP_EXPORT = "topicmap_export.jsp";
  public static final String JSP_TOPIC_EDIT      = "topic_edit.jsp";
  public static final String JSP_TOPIC_VALIDATE  = "topic_validate.jsp";
  public static final String JSP_ASSOC_EDIT      = "assoc_edit.jsp";
  public static final String JSP_ASSOC_VALIDATE  = "assoc_validate.jsp";
  public static final String JSP_SEARCH          = "search.jsp";
  public static final String JSP_HISTORY         = "history.jsp";
  public static final String JSP_EMPTY           = "empty.jsp";
  public static final String JSP_DISPLAY_ERRORS  = "display_errors.jsp";
  
  // frame names (specified for example in form target attribute)
  public static final String FRAME_NAVIGATION    = "navigation";
  public static final String FRAME_EDIT          = "edit";
  public static final String FRAME_SEARCH        = "search";


  // request parameter values belonging to RP_SEARCHCON *AND* RP_LOOKUP_METHOD
  public static final String RPV_SEARCHCON_TYPES            = "topicTypes"; 
  public static final String RPV_SEARCHCON_ASSOC_TYPES      = "assocTypes"; 
  public static final String RPV_SEARCHCON_ASSOC_ROLE_TYPES = "assocRoleTypes"; 
  public static final String RPV_SEARCHCON_ASSOC_THEMES     = "assocThemes"; 
  public static final String RPV_SEARCHCON_OCC_TYPES        = "occTypes"; 
  public static final String RPV_SEARCHCON_BN_THEMES        = "basenameThemes"; 
  public static final String RPV_SEARCHCON_VAR_THEMES       = "variantThemes"; 
  public static final String RPV_SEARCHCON_OCC_THEMES       = "occThemes"; 
  public static final String RPV_SEARCHCON_ASSOC_PLAYER     = "assocPlayer"; 

  // request parameter values belonging to RP_LOOKUP_METHOD
  public static final String RPV_LOOKUPMTD_OBJID            = "object_id";
  public static final String RPV_LOOKUPMTD_INDICATOR        = "subject_indicator";
  public static final String RPV_LOOKUPMTD_SUBJECT          = "subject_address";
  public static final String RPV_LOOKUPMTD_SOURCE           = "source_locator";
  public static final String RPV_LOOKUPMTD_BASENAME         = "basename";
  public static final String RPV_LOOKUPMTD_VARIANT          = "variant";
  public static final String RPV_LOOKUPMTD_ROLETYPE         = "role_type";
  
  // request parameter values belonging to RP_NAV_MODE
  public static final String RPV_NAV_MODE_WELCOME    = "welcome"; 
  public static final String RPV_NAV_MODE_TOPICMAP   = "topicmap"; 
  public static final String RPV_NAV_MODE_TOPIC      = "topic"; 
  public static final String RPV_NAV_MODE_ASSOC      = "assoc"; 

  // request parameter values belonging to RP_NEXTACTION 
  public static final String RPV_NEXTACT_LINK        = "link";

  // request parameter values belonging to RP_TOPIC_TYPE_ID
  public static final String RPV_TYPE_SELECT         = "select";
  public static final String RPV_TYPE_UNTYPED        = "untyped";
  
  // request parameter values belonging to RP_BN_THEME_ID
  public static final String RPV_THEME_SELECT        = "select";
  public static final String RPV_THEME_UNCONSTRAINED = "unconstrained";

  // default request parameter value
  public static final String RPV_DEFAULT  = "__do__";
}
