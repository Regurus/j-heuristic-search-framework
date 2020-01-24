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
package core.algorithms;

import java.util.*;

import core.SearchAlgorithm;
import core.*;
import core.algorithms.SearchResultImpl.SolutionImpl;
import core.SearchResult;
import core.collections.PackedElement;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Recursive Best-First Search
 *
 * @author Matthew Hatem
 */
public class NewBnB implements SearchAlgorithm {

    private SearchResultImpl result;
    private SearchDomain domain;
    private Node goal;
    protected double weight; //weight for heuristic
    private double boundFactor; //above this bound, cut the branch
    private Stack<Node> open; //open list, nodes to check
    private HashSet<PackedElement> visited; // nodes already visited


    private List<Operator> path = new ArrayList<Operator>();

    public NewBnB() {
        this.weight = 1;
    }

    @Override
    public String getName() {
        return "BnB";
    }

    @Override
    public Map<String, Class> getPossibleParameters() {
        return null;
    }

    @Override
    public void setAdditionalParameter(String parameterName, String value) {
        throw new NotImplementedException();
    }

    @Override
    public SearchResult search(SearchDomain domain) {
        //initialize params
        open = new Stack<>();
        visited = new HashSet<>();
        boundFactor = Double.MAX_VALUE;
        this.domain = domain;
        result = new SearchResultImpl();

        result.startTimer();

        State initialState = domain.initialState();
        Node initialNode = new Node(initialState);
        open.add(initialNode);
        visited.add(domain.pack(initialState));
        while (!open.isEmpty()) {
            Node currentNode = this.open.pop();
            State state = currentNode.state;
            visited.remove(domain.pack(state));

            if (domain.isGoal(currentNode.state)) {
                if(boundFactor> currentNode.g/weight){
                    goal = currentNode;
                    boundFactor = currentNode.g/weight;
                }
                continue;
            }

            System.out.println(open.size()+"***openSize***");
            //expand node
            result.expanded++;
            int numOperators = domain.getNumOperators(state);
            List<Node> priority = new ArrayList<>();

            for (int i=0; i< numOperators; ++i) {
                Operator op = domain.getOperator(state, i);
                /*if (op.equals(currentNode.pop)) {
                    continue;
                }*/
                State childState = domain.applyOperator(state, op);
                if(currentNode.parent!= null && childState.equals(currentNode.parent)){
                    System.out.println("WAS HERE!!!");
                    continue;
                }
                PackedElement childPack = domain.pack(childState);
                if (visited.contains(childPack)) {
                    continue;
                }
                result.generated++;
                Node childNode = new Node(childState, currentNode, op, op.reverse(state));

                //add to open stack. else: cut the branch
                if (childNode.g < boundFactor) {
                    visited.add(childPack);
                    //open.add(childNode);
                    priority.add(childNode);
                }
            }
            Collections.sort(priority);
            open.addAll(priority);
            System.out.println("***********current state!!!**************");
            System.out.println(state.convertToString());
            System.out.println("**************************");
            for(int i=0; i<open.size(); i++){
                System.out.println(open.get(i).state.convertToString());
            }
            System.out.print("");
        }
        //finish running the algorithm. restore path
        result.stopTimer();

        if (goal != null) {
            SolutionImpl solution = new SolutionImpl();
            for (Node p = goal; p != null; p = p.parent) {
                path.add(p.op);
            }
            Collections.reverse(path);
            solution.addOperators(path);
            solution.setCost(goal.g);
            result.addSolution(solution);
        }

        return result;
    }

    protected double getRank(Node n) {
        return n.g+ n.h;
    }

    /**
     * The node class
     */
    protected final class Node implements Comparable<Node>{
        double h, g;
        Operator op; //parent to san
        //Operator pop; //san to parent
        State state;
        Node parent;
        // double fPrime;

        private Node(State state) {
            this(state, null, null, null);
        }

        private Node(State state, Node parent, Operator op, Operator pop) {
            double cost = (op != null) ? op.getCost(state, parent.state) : 0;
            this.g = (parent != null) ? parent.g+cost : cost;
            this.h = state.getH();
            this.state = domain.copy(state);
            this.parent = parent;
            //this.pop = pop;
            this.op = op;
        }

        @Override
        public int compareTo(Node o) {
            if (this.g + this.h > o.g + o.h) {
                return 1;
            }
            else{
                return -1;
            }
        }
    }

}
