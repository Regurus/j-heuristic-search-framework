
package core.algorithms;

import core.*;
import core.SearchAlgorithm;
import core.SearchResult;
import core.algorithms.SearchResultImpl.SolutionImpl;
import core.collections.Pair;
import core.domains.RubiksCube;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
public class IDAstar implements SearchAlgorithm {
    // The domain for the search
    protected SearchDomain domain;
    public static Logger log = Logger.getLogger(RubiksCube.class.getName());
    protected SearchResultImpl result;
    protected SolutionImpl solution;
    protected double weight;
    protected double bound;
    protected double minNextF;


    public IDAstar() {
  	    this(1.0);
    }

    public IDAstar(double weight) {
        this.weight = weight;
    } //Changed to public to basically be WIDA*

    @Override
    public String getName() {
        return "idastar";
    }

    @Override
    public Map<String, Class> getPossibleParameters() {
        return null;
    }

    @Override
    public void setAdditionalParameter(String parameterName, String value) {
        switch (parameterName) {
            case "weight": {
                this.weight = Double.parseDouble(value);
                if (this.weight < 1.0d) {
                    System.out.println("[ERROR] The weight must be >= 1.0");
                    throw new IllegalArgumentException();
                } else if (this.weight == 1.0d) {
                    System.out.println("[WARNING] Weight of 1.0 is equivalent to A*");
                }
                break;
            }
            default:{
                throw new NotImplementedException();
            }
        }
    }

    @Override
    public SearchResult search(SearchDomain domain) {
        this.result = new SearchResultImpl();
        this.solution = new SolutionImpl();
        State root = domain.initialState();
        this.result.startTimer();
        this.bound = this.weight * root.getH();
        int i = 0;
        do {
            if(domain.debugMode)
                log.debug("iteration start bound: "+this.bound );
            this.minNextF = -1;
            boolean goalWasFound = this.dfs(domain, root, 0, null);
/*          System.out.println("min next f: " + minNextF ) ;
            System.out.println("next");*/
            this.result.addIteration(i, this.bound, this.result.expanded, this.result.generated);
            this.bound = this.minNextF;
            if(domain.debugMode)
                log.debug("iteration end new bound: "+this.bound );
            if (goalWasFound) {
                break;
            }
        } while (true);
        this.result.stopTimer();

        SearchResultImpl.SolutionImpl solution = new SearchResultImpl.SolutionImpl(this.domain);
        List<Operator> path = this.solution.getOperators();
        List<State> statesPath = this.solution.getStates();

        path.remove(0);
        Collections.reverse(path);
        solution.addOperators(path);

        statesPath.remove(0);
        Collections.reverse(statesPath);
        solution.addStates(statesPath);

        solution.setCost(this.solution.getCost());
        result.addSolution(solution);

        return this.result;
    }

    /**
     * A single iteration of the IDA*
     *
     * @param domain The domain on which the search is performed
     * @param current The current state
     * @param cost The cost to reach the current state
     * @param pop The reverse operator?
     *
     * @return Whether a solution was found
     */
    protected boolean dfs(SearchDomain domain, State current, double cost, Operator pop) {
        double f = cost + this.weight * current.getH();//todo change to potential calculation
        if(domain.debugMode)
            log.debug("current limit: "+this.bound);
        if (f <= this.bound && domain.isGoal(current)) {
            this.solution.setCost(f);
            this.solution.addOperator(pop);
            if(domain.debugMode)
                log.debug("iteration end: goal found @cost "+f);
            return true;
        }
        //todo update the next cost here
        if (f > this.bound) {
            // Let's record the lowest value of f that is greater than the bound
            if (this.minNextF < 0 || f < this.minNextF)
                this.minNextF = f;
            if(domain.debugMode){
                log.debug("branch end: f out of current bound");
            }
            return false;//stopping current iteration
        }

        // Expand the current node
        ++result.expanded;
        int numOps = domain.getNumOperators(current);
        for (int i = 0; i < numOps; ++i) {
            Operator op = domain.getOperator(current, i);
            // Bypass reverse operators
            if (op.equals(pop)) {
                continue;
            }
            ++result.generated;
            State child = domain.applyOperator(current, op);
            boolean goal = this.dfs(domain, child, op.getCost(child, current) + cost, op.reverse(current));
            if (goal) {
                this.solution.addOperator(op);
                this.solution.addState(current);
                return true;
            }
        }

        // No solution was found
        return false;
    }
}

