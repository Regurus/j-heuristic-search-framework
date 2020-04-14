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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import core.SearchAlgorithm;
import core.*;
import core.algorithms.SearchResultImpl.SolutionImpl;
import core.SearchResult;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Recursive Best-First Search
 * 
 * @author Matthew Hatem
 */
public class RBFS extends SearchAlgorithm {
  
	private SearchResultImpl result;
	private SearchDomain domain;
    private Node goal;
    private double weight;
    private List<Operator> path = new ArrayList<Operator>(3);
  
    public RBFS() {
        this(1.0);
    }

    public RBFS(double w) {
        this.weight = w;
    }

    @Override
    public String getName() {
        return "rbfs";
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
        this.domain = domain;

        this.result = new SearchResultImpl();
        this.result.startTimer();

        State initialState = this.domain.initialState();
        Node initialNode = new Node(initialState);
        initialNode.fPrime = weight*initialState.getH();
        rbfs(initialNode, Double.MAX_VALUE);

        this.result.stopTimer();

        if (goal != null) {
            SolutionImpl solution = new SolutionImpl();
            for (Node p = goal; p != null; p = p.parent) {
                path.add(p.op);
            }
            Collections.reverse(path);
                solution.addOperators(path);
                solution.setCost(goal.g);
            this.result.addSolution(solution);
        }
        goal=null;
        return result;
    }

    private double rbfs(Node n, double u) {
        if (goal != null) {
            return Double.MAX_VALUE;
        }

        // goal found
        if (this.domain.isGoal(n.state)) {
            goal = n;
            return n.f;
        }
        if (result.expanded>5000000){
            return -1;
        }

        // generate all successors
        result.expanded++;
        List<Node> succ = new ArrayList<Node>();
        int numOps = this.domain.getNumOperators(n.state);
        for (int i = 0; i < numOps; i++) {
            Operator op = this.domain.getOperator(n.state, i);
            if (op.equals(n.pop)) {
                continue;
            }
            result.generated++;
            State childState = this.domain.applyOperator(n.state, op);
            succ.add(new Node(childState, n, n.state, op, op.reverse(n.state)));
        }

        // no successors
        if (succ.isEmpty()) {
            return Double.MAX_VALUE;
        }

        // update f'
        for (Node s : succ) {
            if (n.f < n.fPrime) {
                s.fPrime = Math.max(s.f, n.fPrime);
            }
            else {
                s.fPrime = s.f;
            }
        }

        // explore virtual fringe
        Collections.sort(succ);
        Node top = succ.get(0);
        while (getRank(top, weight) <= u && top.fPrime < Double.MAX_VALUE) {
            double uPrime = (succ.size() == 1)
                ? u : Math.min(getRank(succ.get(1), weight), u);
            top.fPrime = rbfs(top, uPrime);
            Collections.sort(succ);
            top = succ.get(0);
        }

        return top.fPrime;
    }

    protected double getRank(Node n, double weight) {
      return n.fPrime;
    }

    protected final class Node implements Comparable<Node> {
      double f, g;
      Operator op, pop;
      State state;
      Node parent;
      double fPrime;

      private Node(State state) {
          this(state, null, null, null, null);
      }

      private Node(State state, Node parent, State parentState, Operator op, Operator pop) {
          double cost = (op != null) ? op.getCost(state, parentState) : 0;
          this.g = (parent != null) ? parent.g+cost : cost;
          this.f = g + (weight*state.getH());
          this.state = domain.copy(state);
          this.parent = parent;
          this.pop = pop;
          this.op = op;
      }
      @Override
      public int compareTo(Node that) {
        if (fPrime < that.fPrime) return -1;
        if (fPrime > that.fPrime) return 1;
        return 0;
      }
    }

}
