/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
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

package net.ontopia.persistence.query.sql;

import java.util.Map;

import net.ontopia.persistence.proxy.QueryIF;
import net.ontopia.persistence.proxy.RDBMSAccess;

/**
 * INTERNAL: 
 */

public class RDBMSQuery implements QueryIF {

  protected final RDBMSAccess access;
  protected DetachedQueryIF query;
  
  public RDBMSQuery(RDBMSAccess access, DetachedQueryIF query) {
    this.access = access;
    this.query = query;
  }

  @Override
  public Object executeQuery() throws Exception {
    synchronized (access) {
      return query.executeQuery(access.getConnection());
    }
  }

  @Override
  public Object executeQuery(Object[] params) throws Exception {
    synchronized (access) {
      return query.executeQuery(access.getConnection(), params);
    }
  }

  @Override
  public Object executeQuery(Map params) throws Exception {
    synchronized (access) {
      return query.executeQuery(access.getConnection(), params);
    }
  }
  
}






