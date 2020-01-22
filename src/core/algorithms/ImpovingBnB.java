package core.algorithms;

import core.*;

import java.util.*;

public class ImpovingBnB extends ImprovingSearch {
    SearchDomain domain;
    double cutoff;
    Node currentResult;

    public ImpovingBnB(double suboptimalityBound) {
        this.suboptimalityBound = suboptimalityBound;
    }

    @Override
    protected Node improveSolution(SearchDomain domain, Node solution) {
        this.cutoff = solution.getG()/this.suboptimalityBound;
        this.domain = domain;
        State init = domain.initialState();
        this.dfs(new Node(null,init,0),null);//recursive call
        if(this.currentResult!=null)
            return this.currentResult;
        return solution;
    }

    @Override
    public String getName() {
        return "BnB";
    }

    private void dfs(Node start, Operator reverse){
        double f = start.getG()+start.getCurrent().getH();
        this.expanded++;
        //if this node is a goal and an improvement
        if(this.domain.isGoal(start.getCurrent())){
            this.cutoff = f/this.suboptimalityBound;
            this.currentResult = start;
            return;
            //assumptions here: no negative weights, looking for minimal-length solution (for same cost)
            //therefore no point in continuing the search from here
        }
        int ops = this.domain.getNumOperators(start.getCurrent());
        for(int i=0;i<ops;i++){
            this.generated++;
            Operator nextOperator = this.domain.getOperator(start.getCurrent(),i);
            if(nextOperator.equals(reverse))
                continue;
            State next = domain.applyOperator(start.getCurrent(),nextOperator);
            double nextG = start.getG()+nextOperator.getCost(next,start.getCurrent());
            double nextF = nextG+next.getH();
            if(nextF>this.cutoff) {//pruning rule
                continue;
            }
            this.dfs(new Node(start,next,nextG),nextOperator);
        }

    }
}
