package net.ontopia.tropics.resources;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum QueryParam {
  OFFSET("offset", "o"),
  LIMIT ("limit", "l"),
  INCLUDE("tms-include", "ti"),
  
  HIERARCHY("hierarchy", "h"),
  HAS_SUBTYPE("has-subtype", "hsb"),
  HAS_SUPERTYPE("has-supertype", "hsp"),
  HAS_TYPE("has-type", "ht"),
  HAS_INSTANCE("has-instance", "hi"),
  HAS_PLAYER("has-player", "hp"),
  PLAYS_ROLE("plays-role", "pr"),
  SIBLINGS("sibling-players", "sp"),
  ROLE_IN_ASSOC("role-in-association", "ria"),
  PLAYER_IN_ASSOC("player-in-association", "pia"),
  
  HAS_ROLE("has-role", "hr"),
  HAS_SCOPE("has-scope", "hs"),
  
  QUESTION("question", "qs"),
  QUERY("query", "q"),
  QUERY_LANGUAGE("query-language", "ql")
  ;
  
  
  private final Set<String> aliases = new HashSet<String>();
  
  private QueryParam(String... aliases) {
    for (String alias: aliases) {
      this.aliases.add(alias);
    }
  }

  public Set<String> getAliases() {
    return Collections.unmodifiableSet(aliases);
  }
  
  
  private static final Map<String, QueryParam> aliasToParam = new HashMap<String, QueryParam>();
  
  static {
    for (QueryParam queryParam : values()) {
      for (String alias : queryParam.aliases) {
        aliasToParam.put(alias, queryParam);
      }
    }
  }
  
  public static QueryParam getQueryParam(String alias) {
    return aliasToParam.get(alias);
  }
}
