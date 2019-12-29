package core.algorithms;
/**
 * IDDPS v1: currently optimal due to iterating over f as in IDA*
 * TODO: v2: reformat iterations over potential
 */

import core.Operator;
import core.SearchDomain;
import core.State;
import core.collections.Pair;

import java.util.Comparator;
import java.util.PriorityQueue;

public class IDDPS extends IDAstar{
    double B;
    protected Comparator<Pair<Double,Pair<State,Operator>>> priorityQueComparator = new Comparator<Pair<Double,Pair<State,Operator>>>() {
        @Override
        public int compare(Pair<Double,Pair<State,Operator>> s1, Pair<Double,Pair<State,Operator>> s2) {
            return -(int)Math.ceil(s1.getKey() - s2.getKey());
        }
    };

    public IDDPS(double subOptimalityBound) {
        super(1.0);
        if(subOptimalityBound<1)
            this.B = 1.0;
        else
            this.B = subOptimalityBound;
    }
    @Override
    protected boolean dfs(SearchDomain domain, State parent, double cost, Operator pop) {
        double f = cost + this.weight * parent.getH();

        if (f <= this.bound && domain.isGoal(parent)) {
            this.solution.setCost(f);
            this.solution.addOperator(pop);
            return true;
        }
        if (f > this.bound) {
            // Let's record the lowest value of f that is greater than the bound
            if (this.minNextF < 0 || f < this.minNextF)
                this.minNextF = f;
            return false;//stopping current iteration
        }
        ++result.expanded;
        //building focal list
        PriorityQueue<Pair<Double,Pair<State,Operator>>> focalList =this.buildFocalList(parent,pop,domain);
        //expand by focal list priority
        while(!focalList.isEmpty()){
            Pair<State,Operator> pair = focalList.remove().getValue();
            State next = pair.getKey();
            Operator path = pair.getValue();
            boolean goal = this.dfs(domain, next, path.getCost(next, parent) + cost, path.reverse(parent));
            if (goal) {
                this.solution.addOperator(path);
                this.solution.addState(parent);
                return true;
            }
        }
        // No solution was found
        return false;
    }
    protected PriorityQueue<Pair<Double,Pair<State,Operator>>> buildFocalList(State state, Operator reverse, SearchDomain domain){
        int numOps = domain.getNumOperators(state);
        PriorityQueue<Pair<Double,Pair<State,Operator>>> focalList = new PriorityQueue<>(this.priorityQueComparator);
        for (int i = 0; i < numOps; ++i) {
            Operator op = domain.getOperator(state, i);
            // Bypass reverse operators
            if (op.equals(reverse)) {
                continue;
            }
            ++result.generated;
            State child = domain.applyOperator(state, op);
            focalList.add(new Pair<>(this.getFocalValue(child),new Pair<>(child,op)));
        }
        return focalList;
    }
    protected double getFocalValue(State state) {
        return (this.B*this.solution.getCost()-state.getD())/state.getH();
    }
}
