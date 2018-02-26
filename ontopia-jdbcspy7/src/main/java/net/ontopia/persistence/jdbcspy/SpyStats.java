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

package net.ontopia.persistence.jdbcspy;

import java.io.Writer;
import java.io.IOException;
import net.ontopia.utils.QueryProfiler;

/**
 * INTERNAL.
 */
public class SpyStats {
  protected QueryProfiler profiler = new QueryProfiler();
  
  // -- connection callbacks

  protected synchronized void connectionCommit(SpyConnection conn, long startTime, long endTime) {
    profiler.recordExecute("Connection.commit()", startTime, endTime);
  }

  protected synchronized void connectionRollback(SpyConnection conn, long startTime, long endTime) {
    profiler.recordExecute("Connection.rollback()", startTime, endTime);
  }

  protected synchronized void connectionClose(SpyConnection conn, long startTime, long endTime) {
    profiler.recordExecute("Connection.close()", startTime, endTime);
  }

  // -- prepared statement callbacks

  protected synchronized void preparedExecute(SpyPreparedStatement stm, long startTime, long endTime) {
    profiler.recordExecute(stm.sql, startTime, endTime);
  }

  protected synchronized void preparedExecuteQuery(SpyPreparedStatement stm, long startTime, long endTime) {
    profiler.recordExecute(stm.sql, startTime, endTime);
  }

  protected synchronized void preparedExecuteUpdate(SpyPreparedStatement stm, int affectedSize, long startTime, long endTime) {
    profiler.recordExecuteUpdate(stm.sql, affectedSize, startTime, endTime);
  }

  // -- statement callbacks

  protected synchronized void statementExecute(SpyStatement stm, String sql, long startTime, long endTime) {
    profiler.recordExecute(sql, startTime, endTime);
  }

  protected synchronized void statementExecuteQuery(SpyStatement stm, String sql, long startTime, long endTime) {
    profiler.recordExecute(sql, startTime, endTime);
  }

  protected synchronized void statementExecuteUpdate(SpyStatement stm, String sql, int affectedSize, long startTime, long endTime) {
    profiler.recordExecuteUpdate(sql, affectedSize, startTime, endTime);
  }

  protected synchronized void statementExecuteBatch(SpyStatement stm, String sql, int batchSize, long startTime, long endTime) {
    profiler.recordExecuteUpdate(sql, batchSize, startTime, endTime);
  }

  // -- result set callbacks

  protected synchronized void resultNext(SpyResultSet rs, boolean hasNext, long startTime, long endTime) {
    profiler.recordTraverse(rs.sql, hasNext, startTime, endTime);
  }

  // -- report generation

  protected synchronized void generateReport(Writer out) throws IOException {
    profiler.generateReport("JDBCSpy results", out);
  }
}
