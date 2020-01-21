package core.algorithms;

import core.Operator;
import core.SearchDomain;
import core.State;
import core.collections.Pair;

import java.util.*;


public class IDPS extends IDAstar {

    public IDPS(double weight) {
        super(weight);
    }

    protected HashMap<Integer, Double> sortByPotential(HashMap<Integer,Double> states){
        List<Map.Entry<Integer, Double>> list = new LinkedList<Map.Entry<Integer, Double>>(states.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1,
                               Map.Entry<Integer, Double> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        HashMap<Integer, Double> temp = new LinkedHashMap<Integer, Double>();
        for (Map.Entry<Integer, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    @Override
    protected boolean iterate(SearchDomain domain, State root, double cost, Operator pop) {
        this.domain = domain;
        return this.dfs(domain, new Node(null,root,0),cost,pop);
    }

    protected boolean dfs(SearchDomain domain, Node current, double cost, Operator pop) {
        double f = cost + this.weight * current.getCurrent().getH();
        if(domain.debugMode)
            log.debug("current limit: "+this.bound);
        if (f <= this.bound && domain.isGoal(current.getCurrent())) {
            this.solution.setCost(f);
            this.solution.addOperator(pop);
            if(domain.debugMode)
                log.debug("iteration end: goal found @cost "+f);
            return true;
        }
        //todo update the next cost here
        // Let's record the lowest value of f that is greater than the bound
        if (f > this.bound) {
            this.recordLowestValue(f);
            return false;//stopping current iteration
        }

        // Expand the current node
        ++result.expanded;
        HashMap<Integer,Double> nextStates = this.getAllNextStates(current,pop);
        nextStates = this.sortByPotential(nextStates);
        for(Map.Entry entry : nextStates.entrySet()){
            Operator op = domain.getOperator(current.getCurrent(), (Integer) entry.getKey());
            if (op.equals(pop)) {
                continue;
            }
            ++result.generated;
            State child = domain.applyOperator(current.getCurrent(), op);
            Node next = new Node(current,child,current.getG()+op.getCost(child,current.getCurrent()));
            boolean goal = this.dfs(domain, child, op.getCost(child, current.getCurrent()) + cost, op.reverse(current.getCurrent()));
            if (goal) {
                this.solution.addOperator(op);
                this.solution.addState(current.getCurrent());
                return true;
            }
        }
        // No solution was found
        return false;
    }


    private HashMap<Integer,Double> getAllNextStates(Node state, Operator rev){
        HashMap<Integer,Double> result = new HashMap<>();
        for(int i = 0; i<this.domain.getNumOperators(state.getCurrent()); i++){
            Operator reverse = this.domain.getOperator(state.getCurrent(), i);
            if(reverse.equals(rev))//skipping reverse
                continue;
            State next = this.domain.applyOperator(state.getCurrent(),reverse);
            double g = state.getG()+reverse.getCost(next,state.getCurrent());
            Node nextNode = new Node(state,next,g);
            result.put(i,this.getPotentialValue(nextNode));
        }
        return result;
    }


    protected double getPotentialValue(Node state) {
        //System.out.println(state.getCurrent().getH()/(this.currentC-state.getG()));
        return state.getCurrent().getH()/(this.bound-state.getG());
    }
    private class Node{
        Node previous;
        State current;
        double g;

        public Node(Node previous, State current, double g) {
            this.previous = previous;
            this.current = current;
            this.g = g;
        }

        public double getG() {
            return g;
        }

        public Node getPrevious() {
            return previous;
        }

        public State getCurrent() {
            return current;
        }
    }
}
