/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package core;

import core.collections.PackedElement;

/**
 * The search domain interface.
 *
 * @author Matthew Hatem
 */
public interface SearchDomain extends SearchConfigurable {
    boolean debugMode = false;
    /**
     * Returns the initial state for an instance of Domain.
     *
     *  @return the initial state
     */
    State initialState();

    /**
     * Returns true if the specified state is the goal state, false otherwise.
     *
     * @param state the state
     * @return true if s is a goal state, false otherwise
     */
    boolean isGoal(State state);

    /**
     * Returns the number of operators applicable for the specified state.
     *
     * @param state the state
     * @return the number of operators
     */
    int getNumOperators(State state);

    /**
     * Returns the specified operator applicable for the specified state.
     *
     * @param state the state
     * @param index the nth operator index
     * @return the nth operator
     */
    Operator getOperator(State state, int index);

    /**
     * Applies the specified operator to the specified state and returns an
     * a new edge.
     *
     * @param state the state
     * @param op the operator
     * @return the new edge
     */
    State applyOperator(State state, Operator op);

    /**
     * Returns a copy of the specified state.
     *
     * @param state the state
     * @return the copy
     */
    State copy(State state);

    /**
     * Packs a representation of the specified state into a long.
     *
     * @param state the state
     * @return the packed state as a long
     */
    PackedElement pack(State state);

    /**
     * Unpacks the specified packed representation into a new state.
     *
     * @param packed the long representation
     * @return the new state
     */
    State unpack(PackedElement packed);


    /**
     * This function allows to dump a collection of states based on the domain (e.g. dump all the states of a
     * path-finding problem)
     *
     * NOTE: The implementation of this function is optional and not required for the search
     *
     * @param states The states to dump
     *
     * @return A unified string representation of all the states
     */
    String dumpStatesCollection(State[] states);

    /**
     *
     * @return Whether the currently used heuristic is consistent
     */
    boolean isCurrentHeuristicConsistent();

    /**
     * For tests with oracles, set the optimal cost of the solution
     *
     */
    void setOptimalSolutionCost(double cost);

    /**
     *
     * @return The cost of the optimal solution if defined (or -1 if the cost hasn't been set)
     */
    double getOptimalSolutionCost();

    /**
     *
     * @return the number of instances to generate of this domain before OutOfMemory
     */
    int maxGeneratedSize();
}
