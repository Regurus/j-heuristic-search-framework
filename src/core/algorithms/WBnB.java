package core.algorithms;

import java.util.*;

import core.SearchAlgorithm;
import core.*;
import core.SearchResult;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Recursive Best-First Search
 *
 */
public class WBnB extends SearchAlgorithm {

    private SearchResultImpl result;
    protected double weight; //weight for heuristic
    protected double k; //increase the bound in each iteration k times
    protected double maxExpanded = Double.POSITIVE_INFINITY;

    private List<Operator> path = new ArrayList<Operator>();

    public WBnB(double weight, double multiplierBound) {
        assert weight > 1: "bound factor should increase in each iteration";
        this.weight = weight;
        this.k = multiplierBound;
    }

    public WBnB(double weight, double multiplierBound, int maxExpanded) {
        assert weight > 1: "bound factor should increase in each iteration";
        this.weight = weight;
        this.k = multiplierBound;
        this.maxExpanded = maxExpanded;
    }

    @Override
    public String getName() {
        return "WBnB";
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
        result = new SearchResultImpl();
        result.startTimer();
        State initialState = domain.initialState();
        double boundFactor = initialState.getH() * k;
        SearchResultImpl output;
        BnB bnbBaseSearch = new BnB(weight, boundFactor,(int)this.maxExpanded);
        int i=0;
        do{
            output = (SearchResultImpl) bnbBaseSearch.search(domain);
            result.generated += output.generated;
            result.expanded += output.expanded;
            result.addIteration(i,boundFactor,output.getExpanded(),output.getGenerated());
            bnbBaseSearch.userLimitboundFactor = bnbBaseSearch.userLimitboundFactor * k;
//            i++;
        }while (!output.hasSolution() && result.expanded<this.maxExpanded);
        if(output.getSolutions().size()>0){
            result.addSolution(output.getSolutions().get(0));
        }
        result.stopTimer();
        return result;
    }
    public int getK(){
        return (int)this.k;
    }
}
