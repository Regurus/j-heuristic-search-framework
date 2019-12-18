/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package core;


import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * The search result interface.
 *
 * @author Matthew Hatem
 */
public interface SearchResult {

    /**
     * Returns whether a solution to the problem exists
     *
     * @return true if a solution exists and false otherwise
     */
    public boolean hasSolution();

    /**
     * Returns the solution path.
     *
     * @return the solution path
     */
    public List<Solution> getSolutions();

    /**
     * Returns expanded count in the first iteration of running
     *
     * @return The expanded count in the first iteration of running
     */
    public long getFirstIterationExpanded();

    /**
     * Returns expanded count.
     *
     * @return expanded count
     */
    public long getExpanded();

    /**
     * Returns generated count.
     *
     * @return generated count
     */
    public long getGenerated();

    /**
     * Returns duplicates count.
     *
     * @return Count of the duplicate states
     */
    public long getDuplicates();

    /**
     * Returns the number of duplicate states that were updated in the open list
     *
     * @return duplicates count in the open list
     */
    public long getUpdatedInOpen();

    /**
     * Returns reopened count.
     *
     * @return reopened count
     */
    public long getReopened();

    /**
     *
     * @return extra parameters for this search;
     */
    public TreeMap<String,String> getExtras();

    /**
     * Returns the wall time in milliseconds.
     *
     * @return the wall time in milliseconds
     */
    public long getWallTimeMillis();

    /**
     * Returns the CPU time in milliseconds.
     *
     * @return the CPU time in milliseconds
     */
    public long getCpuTimeMillis();

    /**
     * Increases the statistics by the values of the previous search
     */
    public void increase(SearchResult previous);

    /**
     * Interface for search iterations.
     */

}
