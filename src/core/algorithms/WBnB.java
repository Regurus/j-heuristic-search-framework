package core.algorithms;

import java.util.*;

import core.SearchAlgorithm;
import core.*;
import core.SearchResult;
import core.collections.PackedElement;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Recursive Best-First Search
 *
 */
public class WBnB extends SearchAlgorithm {

    private SearchResultImpl result;
    private SearchDomain domain;
    private Node goal;
    protected double weight; //weight for heuristic
    private double userLimitboundFactor;
    private Stack<Node> open; //open list, nodes to check
    private HashMap<PackedElement, Double> visited; // nodes already visited


    private List<Operator> path = new ArrayList<Operator>();

    public WBnB(double weight) {
        assert weight > 1: "bound factor should increase in each iteration";
        this.weight = weight;
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
        double boundFactor = initialState.getH() * this.weight;
        SearchResultImpl output;
        BnB bnbBaseSearch = new BnB(weight, boundFactor);
        int i=0;
        do{
            output = (SearchResultImpl) bnbBaseSearch.search(domain);
            result.generated += output.generated;
            result.expanded += output.expanded;
            result.addIteration(i,boundFactor,output.getExpanded(),output.getGenerated());
            bnbBaseSearch.userLimitboundFactor = bnbBaseSearch.userLimitboundFactor * this.weight;
//            i++;
        }while (!output.hasSolution());
        result.addSolution(output.getSolutions().get(0));
        result.stopTimer();

        return result;
    }
}