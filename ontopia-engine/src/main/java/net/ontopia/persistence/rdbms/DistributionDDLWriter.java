/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2018 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.persistence.rdbms;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DistributionDDLWriter {
  
  public static final String SCHEMA = "classpath:net/ontopia/topicmaps/impl/rdbms/config/schema.xml";
	public static final String CREATE_PREFIX = ".create.sql";
	public static final String DROP_PREFIX = ".drop.sql";
  
  public static final Map<String, String> PRODUCTS = new LinkedHashMap<>();
  
  static {
    PRODUCTS.put("generic", "generic");
    PRODUCTS.put("oracle", "oracle8,oracle,generic");
    PRODUCTS.put("oracle9i", "oracle9i,oracle,generic");
    PRODUCTS.put("oracle10g", "oracle10g,oracle,generic");
    PRODUCTS.put("sqlserver", "sqlserver,generic");
    PRODUCTS.put("mysql", "mysql,generic");
  }
  
  public static void main(String[] args) throws Exception {
    
    for (Entry<String, String> entry : PRODUCTS.entrySet()) {
      DDLWriter.main(new String[] {SCHEMA, entry.getKey(), entry.getValue(), args[0] + entry.getKey() + CREATE_PREFIX, args[0] + entry.getKey() + DROP_PREFIX});
    }
    
    // add h2 as pure copy of generic
    DDLWriter.main(new String[] {SCHEMA, "generic", "generic", args[0] + "h2" + CREATE_PREFIX, args[0] + "h2" + DROP_PREFIX});
  }
}
