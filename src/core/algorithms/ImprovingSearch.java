package core.algorithms;

import core.*;

import java.util.List;
import java.util.Map;

public abstract class ImprovingSearch extends SearchAlgorithm {
    protected long expanded;
    protected long generated;
    protected double suboptimalityBound;//should be initialized by inheriting classes.

    @Override
    public SearchResult search(SearchDomain domain) {
        Node initial = this.findInitialSolution(domain,new IDAstar(this.suboptimalityBound*4));
        Node bounded = this.improveSolution(domain, initial);
        return this.transformSolution(bounded);
    }

    @Override//deprecated
    public Map<String, Class> getPossibleParameters() {
        return null;
    }

    @Override//deprecated
    public void setAdditionalParameter(String parameterName, String value) {
    }
    /**
     * Stage 1 find initial (any) solution using some suboptimal search algorithm
     * @param domain search domain
     * @param solver search algorithm to search with
     * @return Node with some initial solution
     */
    protected Node findInitialSolution(SearchDomain domain, SearchAlgorithm solver){
        SearchResult result = solver.search(domain);
        System.out.println("initial found");
        return transformSolution(result);
    }

    /**
     * used to decode SearchResult into inner type Node used to communicate with other search algorithms
     * @param solution Solution for the problem
     * @return Node solution
     */
    private Node transformSolution(SearchResult solution){
        if(!solution.hasSolution())
            return null;
        this.expanded = solution.getExpanded();
        this.generated = solution.getGenerated();
        Solution sol = solution.getSolutions().get(0);
        List<State> states = sol.getStates();
        List<Operator> operators = sol.getOperators();
        Node base = null;
        for(State state : states){
            double g = 0;
            if(base!=null)
                g = base.getG()+operators.get(0).getCost(state,base.getCurrent());
            base = new Node(base,state,g);
        }
        return base;
    }

    /**
     * used to encode inner solution format, Node (sort of Linked List) to SearchResult
     * @param solution Solved problem end node
     * @return SearchResult solution
     */
    private SearchResult transformSolution(Node solution){
        SearchResultImpl result = new SearchResultImpl();
        result.setExpanded(this.expanded);
        result.setGenerated(this.generated);
        SearchResultImpl.SolutionImpl sol = new SearchResultImpl.SolutionImpl();
        sol.setCost(solution.getG());
        Node next = solution;
        while (next!=null){
            sol.addState(next.getCurrent());
            next = next.getPrevious();
        }
        result.addSolution(sol);
        return result;
    }

    /**
     * Solution improvement procedure every inheriting class should implement this on his own
     * @param domain search domain
     * @param solution basic solution
     * @return Node encoded solution within needed suboptimality bound
     */
    protected abstract Node improveSolution(SearchDomain domain, Node solution);
    /**
     * inner class node, needed for getting g values and path lookup
     */

}
