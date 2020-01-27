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
public class NewBnB extends SearchAlgorithm {

    class BnBNodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node a, Node b) {
            return Double.compare( b.getCurrent().getD(), a.getCurrent().getD());
        }
    }

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

    public NewBnB(double weight) {
        this.weight = weight;
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
        visited = new HashSet<PackedElement>();
        boundFactor = Double.MAX_VALUE;
        this.domain = domain;
        result = new SearchResultImpl();

        result.startTimer();

        State initialState = domain.initialState();
        Node initialNode = new Node(null, initialState, 0);
        open.add(initialNode);
        visited.add(domain.pack(initialState));
        while (!open.isEmpty()) {
            Node currentNode = this.open.pop();
            State state = currentNode.getCurrent();
            visited.remove(domain.pack(state));

            if (domain.isGoal(currentNode.getCurrent())) {
                System.out.println("Found!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println(boundFactor);
                if(boundFactor> currentNode.getG()/weight){
                    goal = currentNode;
                    boundFactor = currentNode.getG()/weight;
                }
                continue;
            }

            System.out.println(open.size()+"**openSize**");
            //expand node
            result.expanded++;
            int numOperators = domain.getNumOperators(state);
            List<Node> priority = new ArrayList<>();

            for (int i=0; i< numOperators; ++i) {
                Operator op = domain.getOperator(state, i);
                State childState = domain.applyOperator(state, op);
                if(currentNode.getPrevious()!= null && childState.equals(currentNode.getPrevious().getCurrent())){
//                    System.out.println("WAS HERE!!!");
                    continue;
                }
                PackedElement childPack = domain.pack(childState);
                if (!visited.add(childPack)) {
                    System.gc();
                    continue;
                }
                result.generated++;
                Node childNode = new Node(currentNode, childState, currentNode.getG()+op.getCost(childState, state));

                //add to open stack. else: cut the branch
                if (childNode.getF() < boundFactor) {
                    //open.add(childNode);
                    priority.add(childNode);
                }
            }
            priority.sort(new BnBNodeComparator());
            open.addAll(priority);
        }
        System.out.println("cost"+goal.getG());
        //finish running the algorithm. restore path
        result.stopTimer();

        if (goal != null) {
            SolutionImpl solution = new SolutionImpl();
            for (Node p = goal; p != null; p = p.getPrevious()) {
                //path.add(p);
            }
            Collections.reverse(path);
            solution.addOperators(path);
            //solution.setCost(goal.g);
            result.addSolution(solution);
        }

        return result;
    }
}