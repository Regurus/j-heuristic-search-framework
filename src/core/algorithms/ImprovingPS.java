package core.algorithms;

import core.Operator;
import core.SearchDomain;
import core.State;
import core.collections.Pair;

import java.util.*;


public class ImprovingPS extends ImprovingSearch {

    private static double currentC;
    private Node currentSolution;

    public ImprovingPS(double suboptimalityBound) {
        this.suboptimalityBound = suboptimalityBound;
    }

    @Override
    protected Node improveSolution(SearchDomain domain, Node solution) {
        boolean canBeImproved = true;
        this.domain = domain;
        currentC = solution.getG()/this.suboptimalityBound;
        while (canBeImproved){
            canBeImproved = this.iteratePTS(new Node(null,domain.initialState(),0,0),null);
            if(canBeImproved)
                currentC = this.currentSolution.getG()/this.suboptimalityBound;
        }
        if(currentSolution!=null)
            return currentSolution;
        return solution;//no improvement achieved
    }

    @Override
    public String getName() {
        return "IDPS";
    }

    private boolean iteratePTS(Node initial,SearchDomain domain){
        boolean ans = false;
        for(int i=5;i<currentC+1;i+=Math.ceil(currentC*0.1)){
            System.out.println("max depth: "+i);
            ans = this.PTSIteration(initial,null,i);
            if(ans)
                break;
        }
        return ans;
    }

    //returns false if no improvement achieved
    private boolean PTSIteration(Node current,Operator reverse,int maxDepth){
        if(current.getG()>=currentC)
            return false;
        if(current.getDepth()>=maxDepth)
            return false;
        boolean answer =false;
        if(this.domain.isGoal(current.getCurrent())){
            this.currentSolution = current;
            return true;
        }
        Pair<Node,Operator>[] ordered = this.orderByComparator(current,reverse,new ByKeyComparator());
        this.expanded++;
        for(Pair<Node,Operator> next : ordered){
            //one positive answer is enough to make whole run finish
            answer = answer || this.PTSIteration(next.getKey(),next.getValue(),maxDepth);
            if(answer)
                break;
        }
        return answer;
    }

    private static double getPotential(Node node){
        return node.getCurrent().getH()/(currentC-node.getG());
    }

    private class ByKeyComparator implements Comparator<Pair<Node,Operator>>{
        public int compare(Pair<Node,Operator> s1, Pair<Node,Operator> s2) {
            if (getPotential(s1.getKey()) < getPotential(s2.getKey()))
                return 1;
            else if (getPotential(s1.getKey()) > getPotential(s2.getKey()))
                return -1;
            return 0;
        }
    }
}
