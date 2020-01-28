package core.algorithms;

import core.*;
import core.collections.Pair;

import java.util.*;

public class ImprovingBnB extends ImprovingSearch {
    double cutoff;
    Node currentResult;

    public ImprovingBnB(double suboptimalityBound) {
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
            System.out.println("cutoff updated: "+this.cutoff + "to: "+f/this.suboptimalityBound);
            this.cutoff = f/this.suboptimalityBound;
            this.currentResult = start;
            return;
            //assumptions here: no negative weights, looking for minimal-length solution (for same cost)
            //therefore no point in continuing the search from here
        }
        Pair<Node,Operator>[] nextList = this.orderByComparator(start,reverse,new ByKeyComparator());
        for(Pair<Node,Operator> nextPair:nextList){
            this.generated++;
            if(nextPair.getValue().equals(reverse))
                continue;
            State next = domain.applyOperator(start.getCurrent(),nextPair.getValue());
            double nextG = start.getG()+nextPair.getValue().getCost(next,start.getCurrent());
            double nextF = nextG+next.getH();
            if(nextF>this.cutoff) {//pruning rule
                continue;
            }
            this.dfs(new Node(start,next,nextG),nextPair.getValue());
        }
        /*
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
        }*/
    }

    private class ByKeyComparator implements Comparator<Pair<Node,Operator>>{
        public int compare(Pair<Node,Operator> s1, Pair<Node,Operator> s2) {
            if (s1.getKey().getG()+s1.getKey().getCurrent().getH() > s2.getKey().getG()+s2.getKey().getCurrent().getH())
                return 1;
            else if (s1.getKey().getG()+s1.getKey().getCurrent().getH() < s2.getKey().getG()+s2.getKey().getCurrent().getH())
                return -1;
            return 0;
        }
    }
}
