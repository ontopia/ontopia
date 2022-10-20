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

package net.ontopia.topicmaps.query.impl.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;
import net.ontopia.topicmaps.query.parser.Pair;
import net.ontopia.topicmaps.query.parser.Variable;

/**
 * INTERNAL: Implements association type predicates.
 */
public class DynamicAssociationPredicate extends AbstractDynamicPredicate {

  // -- Prefetcher constants
  
  private final static int[] Prefetcher_RBT_fields = 
    new int[] { Prefetcher.AssociationRoleIF_association,
                Prefetcher.AssociationIF_roles,
                Prefetcher.AssociationRoleIF_player };
  
  private final static boolean[] Prefetcher_RBT_traverse =
    new boolean[] { true, false, false }; // ISSUE: traverse R.player?

  protected TopicMapIF topicmap;
  protected ClassInstanceIndexIF index;
  
  public DynamicAssociationPredicate(TopicMapIF topicmap, LocatorIF base, TopicIF type) {
    super(type, base);
    this.topicmap = topicmap;
    
    index = (ClassInstanceIndexIF) topicmap.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
  }

  @Override
  public String getSignature() {
    return "p+";
  }

  @Override
  public int getCost(boolean[] boundparams) {
    int open = 0;
    int closed = 0;
    for (int ix = 0; ix < boundparams.length; ix++) {
      if (!boundparams[ix]) {
        open++;
      } else {
        closed++;
      }
    }

    if (open == 0) {
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else if (closed > 0) {
      return PredicateDrivenCostEstimator.MEDIUM_RESULT - closed;
    } else {
      return PredicateDrivenCostEstimator.BIG_RESULT - closed;
    }
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {
    
    // check whether to use a faster implementation
    int argix = -1;
    for (int i = 0; i < arguments.length; i++) {
      Pair pair = (Pair) arguments[i];
      int colno = matches.getIndex(pair.getFirst());
      if (matches.bound(colno)) {
        argix = i;
        break;
      }
    }
    if (argix != -1) {
      return satisfyWhenBound(matches, arguments, argix);
    }
    
    // initialize
    QueryMatches result = new QueryMatches(matches);
    AssociationRoleIF[] seed2 = new AssociationRoleIF[2]; // most assocs are binary
    ArgumentPair[] bound = getBoundArguments(matches, arguments, -1);
    ArgumentPair[] unbound = getUnboundArguments(matches, arguments);
    int colcount = matches.colcount; // time-saving shortcut
    Object[][] data = matches.data;  // ditto

    // loop over associations
    AssociationIF[] assocs = index.getAssociations(type).toArray(new AssociationIF[0]);

    // prefetch roles
    Prefetcher.prefetch(topicmap, assocs, 
                        Prefetcher.AssociationIF, 
                        Prefetcher.AssociationIF_roles, false);

    int bound_length = bound.length;
    int unbound_length = unbound.length;
    boolean[] roleused = new boolean[10];

    for (AssociationIF assoc : assocs) {
      Collection<AssociationRoleIF> rolecoll = assoc.getRoles();
      AssociationRoleIF[] roles = rolecoll.toArray(seed2);
      int roles_length = rolecoll.size();
      if (roles_length > roleused.length) {
        roleused = new boolean[roles_length];
      }

      // loop over existing matches
      for (int row = 0; row <= matches.last; row++) {
        // blank out array of used roles
        for (int roleix = 0; roleix < roles_length; roleix++) {
          roleused[roleix] = false;
        }
        
        // check bound columns against association
        boolean ok = true;
        for (int colix = 0; colix < bound_length; colix++) {
          TopicIF roleType = bound[colix].roleType;
          int col = bound[colix].ix;
          
          // find corresponding role
          ok = false;
          for (int roleix = 0; roleix < roles_length; roleix++) {
            if (!roleused[roleix] &&
                roleType.equals(roles[roleix].getType()) &&
                data[row][col].equals(roles[roleix].getPlayer())) {
              ok = true;
              roleused[roleix] = true;
              break;
            }
          }
          
          if (!ok) { // no matching role, so don't bother checking more columns
            break;
          }
        }

        if (!ok) { // match failed, so try next row
          continue;
        }

        // produce all possible combinations of role bindings
        while (true) {
          boolean one_unused_role = false; // it's ok so long as *one* role was unused
        
          // produce match by binding unbound columns
          for (int colix = 0; colix < unbound_length; colix++) {
            TopicIF roleType = unbound[colix].roleType;
          
            // find corresponding role
            int role = -1;
            for (int roleix = 0; roleix < roles_length; roleix++) {
              if (roleType.equals(roles[roleix].getType())) {
                if (roles[roleix].getPlayer() == null) {
                  roleused[roleix] = true; // don't touch this again
                  continue; // keep looking
                }
                
                role = roleix; // this role is a candidate for use
                
                if (!roleused[roleix]) {
                  one_unused_role = true;
                  break; // this role was unused, so let's use it;
                         // otherwise keep looking
                }
              }
            }
            if (role == -1) {
              one_unused_role = false; // this makes sure no match is produced
              break; // no role found, so give up
            }

            // ok, there is an association role matching this unbound column
            roleused[role] = true;
            unbound[colix].boundTo = roles[role].getPlayer();
          }

          if (!one_unused_role) {
            break; // no combos where one role unused
          }

          // ok, the row/assoc combo is fine; now make a match for it
          if (result.last+1 == result.size) {
            result.increaseCapacity();
          }
          result.last++;
        
          System.arraycopy(data[row], 0, result.data[result.last], 0, colcount);
          for (int colix = 0; colix < unbound_length; colix++) {
            // we may have had multiple arguments for the same unbound
            // column, so have to check whether they matched up
            Object value = result.data[result.last][unbound[colix].ix];
            if ((value == null || value.equals(unbound[colix].boundTo)) &&
                unbound[colix].boundTo != null) {
              result.data[result.last][unbound[colix].ix] = unbound[colix].boundTo;
            } else {
              // this match is bad. we need to retract it
              result.last--; // all cols reset when new matches made, anyway
            }
          }
        } // while(true)

      } // for each row in existing matches
    } // for each association

    // check if we have a symmetrical query, and if so, mirror the
    // symmetrical values
    mirrorIfSymmetrical(result, arguments);

    return result;
  }

  /**
   * INTERNAL: Faster version of satisfy for use when one variable has
   * already been bound, because it is much faster in that case. It is
   * faster because it does not need to do the full all associations x
   * all matches comparison. It is used instead of the naive one when
   * heuristics indicate that this is the best approach.
   *
   * @param matches The query matches passed in to us.
   * @param arguments The arguments passed to the predicate.
   * @param argix The argument to start from.
   */
  private QueryMatches satisfyWhenBound(QueryMatches matches,
                                        Object[] arguments,
                                        int argix)
    throws InvalidQueryException {

    // initialize
    // boundcol: column in matches where start argument is bound
    int boundcol = matches.getIndex(((Pair) arguments[argix]).getFirst());
    QueryMatches result = new QueryMatches(matches);
    AssociationRoleIF[] seed2 = new AssociationRoleIF[2]; // most assocs are binary
    ArgumentPair[] bound = getBoundArguments(matches, arguments, argix);
    ArgumentPair[] unbound = getUnboundArguments(matches, arguments);
    int colcount = matches.colcount; // time-saving shortcut
    Object[][] data = matches.data;  // ditto

    int bound_length = bound.length;
    int unbound_length = unbound.length;
    TopicIF rtype = (TopicIF) ((Pair) arguments[argix]).getSecond();

    // pre-allocating this to save time
    boolean[] roleused = new boolean[25];
    
    Prefetcher.prefetchRolesByType(topicmap, matches, boundcol, rtype, type,
                                   Prefetcher_RBT_fields,
                                   Prefetcher_RBT_traverse);

//     // in the in-memory implementation the getRolesByType() call often consumes
//     // much of the time needed to run a query. we solve this by implementing a
//     // simple role cache. using an LRUMap to avoid making a cache that grows
//     // beyond all reasonable bounds.
//     java.util.Map rolecache =
//       new org.apache.commons.collections.map.LRUMap(100);
    
    // loop over existing matches
    for (int row = 0; row <= matches.last; row++) {

      // verify that we're looking at a topic
      if (!(data[row][boundcol] instanceof TopicIF)) {
        continue; // this can't be a valid row
      }
      
      // now, test if this row is really valid
      TopicIF topic = (TopicIF) data[row][boundcol];

      // first, look for roles in assocs of the type we're supposed to have
      for (AssociationRoleIF arole : topic.getRolesByType(rtype, type)) {

        // ok, we've found the role; now let's see if the association
        // can produce a match
        // --------------------------------------------------------------
        Collection<AssociationRoleIF> rolecoll = arole.getAssociation().getRoles();
        AssociationRoleIF[] roles = rolecoll.toArray(seed2);
        int roles_length = rolecoll.size();

        // check bound arguments against association
        boolean ok = true;
        for (int arg = 0; arg < bound_length; arg++) {
          TopicIF roleType = bound[arg].roleType;
          int col = bound[arg].ix;

          // find corresponding role
          int role = -1;
          for (int roleix = 0; roleix < roles_length; roleix++) {
            if (roleType.equals(roles[roleix].getType()) &&
                data[row][col] != null && // bug #2001
                data[row][col].equals(roles[roleix].getPlayer())) {
              role = roleix;
              break;
            }
          }

          if (role == -1) { // no matching role found
            ok = false;
            break;
          }
        }
        if (!ok) {
          continue; // this assoc didn't match
        }

        // produce match by binding unbound columns
        if (roles_length > roleused.length) {
          roleused = new boolean[roles_length];
        }
        for (int roleix = 0; roleix < roles_length; roleix++) {
          roleused[roleix] =
            // if this is the start role then that's already used
            topic.equals(roles[roleix].getPlayer()) &&
            rtype.equals(roles[roleix].getType());
        }

        for (int arg = 0; arg < unbound_length; arg++) {
          TopicIF roleType = unbound[arg].roleType;

          // find corresponding role
          int role = -1;
          for (int roleix = 0; roleix < roles_length; roleix++) {
            if (roleType.equals(roles[roleix].getType()) &&
                !roleused[roleix]) {
              role = roleix;
              break;
            }
          }
          if (role == -1) {
            ok = false;
            break;
          }

          // won't accept null players
          if (roles[role].getPlayer() == null) {
            ok = false;
            break;
          }

          // ok, there is an association role matching this unbound column
          unbound[arg].boundTo = roles[role].getPlayer();
          roleused[role] = true;
        }

        if (ok) {
          // ok, the row/assoc combo is fine; now make a match for it
          if (result.last+1 == result.size) {
            result.increaseCapacity();
          }
          result.last++;

          System.arraycopy(data[row], 0, result.data[result.last], 0, colcount);
          for (int arg = 0; arg < unbound_length && ok; arg++) {
            result.data[result.last][unbound[arg].ix] = unbound[arg].boundTo;
            unbound[arg].boundTo = null;
          }
        }
// ----------------------------------------------------------------------
      }
    }
    
    QueryTracer.trace("  results: " + result.last);
    return result;
  }
  
  // --- Internal methods

  private void mirrorIfSymmetrical(QueryMatches result, Object[] arguments) {

    int col1 = -1;
    int col2 = -1;
    for (int ix1 = 0; ix1 < arguments.length; ix1++) {
      Pair arg1 = (Pair) arguments[ix1];
      if (!(arg1.getFirst() instanceof Variable)) {
        continue;
      }
      
      for (int ix2 = ix1+1; ix2 < arguments.length; ix2++) {
        Pair arg2 = (Pair) arguments[ix2];
        if (!(arg2.getFirst() instanceof Variable)) {
          continue;
        }
        
        if (arg1.getSecond().equals(arg2.getSecond())) {
          col1 = result.getIndex((Variable) arg1.getFirst());
          col2 = result.getIndex((Variable) arg2.getFirst());
          break; // FIXME: should really produce all combinations and repeat op for
                 // each combination
        }
      }
    }

    if (col1 == -1) {
      return; // no symmetry, so nothing to do
    }

    result.ensureCapacity((result.last+1) * 2);

    Object[][] data = result.data;
    int next = result.last + 1;
    int width = result.colcount;

    for (int ix = 0; ix <= result.last; ix++) {
      data[next] = new Object[width];
      System.arraycopy(data[ix], 0, data[next], 0, width);
      data[next][col1] = data[ix][col2];
      data[next][col2] = data[ix][col1];
      next++;
    }

    result.last = next-1;
  }
  
  protected ArgumentPair[] getBoundArguments(QueryMatches matches,
                                             Object[] arguments,
                                             int boundarg)
    throws InvalidQueryException {

    int width = arguments.length;
    List<ArgumentPair> args = new ArrayList<ArgumentPair>(width);
    for (int ix = 0; ix < width; ix++) {
      if (ix == boundarg) {
        continue; // yes, this is bound, but since we're starting from it
                  // we can ignore it
      }
      
      Object arg = arguments[ix];
      if (!(arg instanceof Pair)) {
        throw new InvalidQueryException("Invalid argument to association predicate (only pairs allowed)");
      }
      
      Pair pair = (Pair) arg;
      if (!(pair.getSecond() instanceof TopicIF)) {
        throw new InvalidQueryException("Second half of association predicate pair argument must be a topic constant; found '" + pair + "'");
      }

      
      int colno = matches.getIndex(pair.getFirst());
      if (matches.bound(colno)) {
        args.add(new ArgumentPair(colno, (TopicIF) pair.getSecond()));
      }
    }

    if (args.isEmpty()) {
      return new ArgumentPair[0];
    }
    return args.toArray(new ArgumentPair[args.size()]);
  }

  protected ArgumentPair[] getUnboundArguments(QueryMatches matches,
                                               Object[] arguments)
    throws InvalidQueryException {
    
    int width = arguments.length;
    List<ArgumentPair> args = new ArrayList<ArgumentPair>(width);
    for (int ix = 0; ix < width; ix++) {
      Pair pair = (Pair) arguments[ix];
      if (!(pair.getSecond() instanceof TopicIF)) {
        throw new InvalidQueryException("Second half of association predicate pair argument must be a topic constant");
      }
      
      int colno = matches.getIndex(pair.getFirst());
      if (matches.data[0][colno] == null) {
        args.add(new ArgumentPair(colno, (TopicIF) pair.getSecond()));
      }
    }

    if (args.isEmpty()) {
      return new ArgumentPair[0];
    }
    return args.toArray(new ArgumentPair[args.size()]);
  }
  
  // --- Argument class

  protected class ArgumentPair {
    public int ix;
    public TopicIF roleType;
    public TopicIF boundTo;  // used to store binding during evaluation

    public ArgumentPair(int ix, TopicIF roleType) {
      this.ix = ix;
      this.roleType = roleType;
    }

    @Override
    public String toString() {
      return "<AP$ArgPair " + ix + ":" + roleType + ">";
    }
  }
}
