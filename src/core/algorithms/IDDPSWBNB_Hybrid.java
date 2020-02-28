package core.algorithms;

import core.Operator;
import core.SearchDomain;
import core.State;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class IDDPSWBNB_Hybrid extends IDDPS {
    public IDDPSWBNB_Hybrid(double weight) {
        super(weight);
    }

    public IDDPSWBNB_Hybrid() {
    }

    @Override
    protected ArrayList<IDDPS.ComparableState> getNextNodes(State parent, Operator pop, double cost, SearchDomain domain) {
        ArrayList<ComparableState> result = new ArrayList<>();
        PriorityQueue<ComparableState> queue = new PriorityQueue<>();
        for(int i=0; i<domain.getNumOperators(parent);i++){
            Operator op = domain.getOperator(parent, i);
            if (op.equals(pop)) {
                continue;
            }
            this.result.generated++;
            State child = domain.applyOperator(parent, op);
            if(child.getH()+cost+op.getCost(child,parent)>this.bound)
                continue;
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
}
