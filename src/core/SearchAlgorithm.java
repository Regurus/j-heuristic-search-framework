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


/**
 * The search algorithm abstract class.
 *
 */
public abstract class SearchAlgorithm implements SearchConfigurable{
    protected double maxExpanded = Double.POSITIVE_INFINITY;
    public abstract String getName();

    /**
     * Performs a search beginning at the specified state.
     *
     * @param domain The domain to apply the search on
     * @return search results
     */
    public abstract SearchResult search(SearchDomain domain);

    /**
     * basic node class allows retrieval of g value and depth as well as works as linked list
     * and allows to extract path to the node
     */
    protected class Node{
        private Node previous;
        private State current;
        private int depth = 0;
        private double g;
        public Operator op;

        public Node(Node previous, State current, double g) {
            this.previous = previous;
            this.current = current;
            this.g = g;
        }

        public Node(Node previous, State current, double g, int depth) {
            this.previous = previous;
            this.current = current;
            this.g = g;
            this.depth = depth;
        }

        public Node(Node previous, State current, double g, Operator op) {
            this.previous = previous;
            this.current = current;
            this.g = g;
            this.op = op;
        }

        public double getG() {
            return g;
        }

        public Node getPrevious() {
            return previous;
        }

        public double getF() {
            return current.getH()+g;
        }

        public State getCurrent() {
            return current;
        }

        public int getDepth() {
            return depth;
        }
    }
}
