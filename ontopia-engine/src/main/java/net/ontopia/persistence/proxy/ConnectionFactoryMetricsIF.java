/*-
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2025 The Ontopia Project
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

package net.ontopia.persistence.proxy;

interface ConnectionFactoryMetricsIF extends ConnectionFactoryIF {
  default long getConnectionPoolActive() { return -1; }
  default long getConnectionPoolIdle() { return -1; }
  default long getConnectionPoolMaxActive() { return -1; }
  default long getConnectionPoolMaxIdle() { return -1; }
  default long getConnectionPoolMinIdle() { return -1; }

  default long getConnectionsClosed() { return -1; }
  default long getConnectionsOpened() { return -1; }
  default long getConnectionsValidated() { return -1; }
  default long getConnectionsBorrowed() { return -1; }
  default long getConnectionsReturned() { return -1; }
}
