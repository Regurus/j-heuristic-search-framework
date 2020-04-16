package core.algorithms;

import core.Operator;
import core.SearchDomain;
import core.State;
import core.collections.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * IDDPS v1: currently optimal due to iterating over f as in IDA*
 * IDDPS: v2: deprecated due to high degree of similarity with WIDA* no obvious advantages over it.
 * IDDPS: v3: DFS search for initial solution on priority insertion based on
 */


public class IDDPS extends IDAstar {
    public IDDPS(double weight) {
        super(weight);
    }

    public IDDPS(double weight, int maxNodes){
        super(weight,maxNodes);
    }

    public IDDPS() {
        super();
    }

    @Override
    protected boolean dfs(SearchDomain domain, State parent, double cost, Operator pop) {
        double f = cost + this.weight * parent.getH();
        if(this.checkIfSolution(domain,f,pop,parent))
            return true;
        if(!this.recordLowestValue(f))
            return false;
        return this.expandNode(parent,pop,cost,domain);
    }

    private boolean recordLowestValue(double f){
        if (f > this.bound) {
            // Let's record the lowest value of f that is greater than the bound
            if (this.minNextF < 0 || f < this.minNextF)
                this.minNextF = f;
            return false;
        }
        return true;
    }

    private boolean checkIfSolution(SearchDomain domain,double f, Operator pop,State parent){
        if (f <= this.bound && domain.isGoal(parent)) {
            this.solution.setCost(f);
            this.solution.addOperator(pop);
            this.solution.addState(parent);
            return true;
        }
        return false;
    }

    private boolean expandNode(State parent, Operator pop, double cost, SearchDomain domain) {
        if(this.result.expanded>this.maxExpanded)
            return false;
        result.expanded++;
        ArrayList<ComparableState> states = this.getNextNodes(parent,pop,cost,domain);
        for(ComparableState next: states){
            State child = next.getState();
            Operator op = next.getOperator();
            boolean goal = this.dfs(domain, child, op.getCost(child, parent) + cost, op.reverse(parent));
            if (goal) {
                this.solution.addOperator(op);
                this.solution.addState(parent);
                return true;
            }
        }
        return false;
    }

    protected ArrayList<ComparableState> getNextNodes(State parent, Operator pop, double cost,SearchDomain domain){
        ArrayList<ComparableState> result = new ArrayList<>();
        PriorityQueue<ComparableState> queue = new PriorityQueue<>();
        for(int i=0; i<domain.getNumOperators(parent);i++){
            Operator op = domain.getOperator(parent, i);
            if (op.equals(pop)) {
                continue;
            }
            this.result.generated++;
            State child = domain.applyOperator(parent, op);
            queue.add(new ComparableState(child,op,cost,this.bound));
        }
        //System.out.println("iteration header---------------------------");
        while (!queue.isEmpty()){
            //ComparableState next = queue.poll();
            //System.out.println(next.getWeight());
            result.add(queue.poll());
        }
        //System.out.println("iteration footer----------------------------");
        return result;
    }

    protected class ComparableState implements Comparable{
        private State state;
        private Operator operator;
        private double cost;
        private double bound;

        ComparableState(State state, Operator operator, double cost,double f) {
            this.state = state;
            this.operator = operator;
            this.cost = cost;
            this.bound = f;
        }

        public State getState() {
            return state;
        }

        public Operator getOperator() {
            return operator;
        }

        public double getCost() {
            return cost;
        }

        @Override
        public int compareTo(Object o) {
            if(!(o instanceof ComparableState))
                return 0;
            ComparableState other = (ComparableState)o;
            double res = other.getWeight()-this.getWeight();
            if(res>0)
                return 1;
            else if (res<0)
                return -1;
            else return 0;
        }
        public double getWeight(){
            return cost/state.getH();
        }
    }
}
