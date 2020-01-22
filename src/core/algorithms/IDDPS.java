package core.algorithms;
/**
 * IDDPS v1: currently optimal due to iterating over f as in IDA*
 * IDDPS: v2: deprecated due to high degree of similarity with WIDA* no obvious advantages over it.
 * IDDPS: v3: DFS search for initial solution on priority insertion based on
 */

import core.*;
import core.collections.Pair;
import java.util.*;
import java.util.LinkedHashMap;
import core.algorithms.SearchResultImpl.SolutionImpl;

public class IDDPS extends SearchAlgorithm {

    Stack<Pair<Node,Operator>> openList;
    private double maxF = 0;
    private double currentC = 0;//maybe we should update it with maxH*W
    private boolean solutionFound;
    private boolean noImprovementPossible;
    private boolean inLookup;
    private double solutionCost;
    private double W;
    int iteration = 0;
    private Node currentSolution;
    private SearchDomain domain = null;
    private SearchResultImpl result;




    public IDDPS(double subOptimalityBound) {
        if(subOptimalityBound<1)
            this.W = 1.0;
        else
            this.W = subOptimalityBound;
    }

    @Override
    public String getName() {
        return "IDDPS";
    }

    @Override
    public SearchResult search(SearchDomain domain) {
        this.openList = new Stack<>();
        this.domain = domain;
        this.solutionFound = false;
        this.noImprovementPossible = false;
        this.result = new SearchResultImpl();
        this.inLookup = true;
        this.solutionCost = Double.POSITIVE_INFINITY;
        this.currentSolution = null;
        State init = domain.initialState();
        //insert heuristic value control logic here TODO: take care of case in which init node`s heuristic is zero
        this.maxF = init.getH();
        this.iteration = 0;
        while(!this.noImprovementPossible){
            System.out.println("Iteration: "+this.iteration++ + " maxF: "+this.maxF);
            if(!this.solutionFound){//stage 1 looking for any solution
                if(this.maxF == this.currentC)//in case no solution found and no new f value discovered
                    this.maxF++;//TODO: calibrate increment value
                this.currentC = this.maxF;//*this.W ???*/
                this.openList.push(new Pair<>(new Node(null,init,0),null));
            }
            else{//stage 2 looking to improve the solution
                this.openList.push(new Pair<>(new Node(null,init,0),null));
                this.currentC = this.solutionCost/this.W;
            }
            Node solution = this.potentialSearchIteration(this.currentC);
            if(solution!=null)
                this.currentSolution = solution;
        }
        return this.reconstructPath(this.currentSolution);
    }

    @Override
    public Map<String, Class> getPossibleParameters() {
        //deprecated
        return new HashMap<String, Class>();
    }

    @Override
    public void setAdditionalParameter(String parameterName, String value) {
        //deprecated
    }

    /**
     * a potential search session used all over the iterations either improvement or lookup
     * this version of PS(PTS) based on beam search strategy to comply with linear memory bound
     * @param bound max cost for the solution (known as C in original potential search)
     */
    Node potentialSearchIteration(double bound){
        while (!this.openList.isEmpty()){
            Pair<Node,Operator> next = this.openList.pop();
            if(this.domain.isGoal(next.getKey().getCurrent()) && next.getKey().getG() <= bound){
                System.out.println("SOLUTION!!! Cost: "+next.getKey().getG());
                if(this.inLookup){
                    System.out.println("Lookup END; expanded: "+this.result.expanded+" generated: "+this.result.generated);
                }
                this.inLookup = false;
                if(!this.solutionFound)
                    this.solutionFound = true;
                this.solutionCost = next.getKey().getG();
                return next.getKey();
            }
            if(next.getKey().getG() < bound ){
                this.expandNode(next.getKey(),next.getValue());
                //System.out.println(next.getKey().getCurrent().getH()/(this.currentC-next.getKey().getG()));
            }
        }
        if(!this.inLookup)
            this.noImprovementPossible = true;
        return null;
    }
    /**
     * expand a single node
     * @param state state to be expanded
     * @param reverse operator sourced the state (reverse operator for it)
     */
    protected void expandNode(Node state, Operator reverse){
        HashMap<Pair<Node,Operator>,Double> nextStates = this.getAllNextStates(state,reverse);
        HashMap<Pair<Node,Operator>, Double> sorted = this.sortByPotential(nextStates);
        this.result.expanded++;
        this.insertToOpen(sorted);
    }

    /**
     * gets all states available from given state and returns them to be inserted into the stack by some order
     * @param state state to expand
     * @param rev it`s reverse operator(path leading to it)
     * @return map pairs next states and their reverse operators, mapped to their potential value
     */
    private HashMap<Pair<Node,Operator>,Double> getAllNextStates(Node state, Operator rev){
        HashMap<Pair<Node,Operator>,Double> result = new HashMap<>();
        for(int i = 0; i<this.domain.getNumOperators(state.getCurrent()); i++){
            Operator reverse = this.domain.getOperator(state.getCurrent(), i);
            if(reverse.equals(rev))//skipping reverse
                continue;
            State next = this.domain.applyOperator(state.getCurrent(),reverse);
            double g = state.getG()+reverse.getCost(next,state.getCurrent());
            if(g+next.getH()>this.maxF)
                this.maxF = g+next.getH();
            Node nextNode = new Node(state,next,g);
            result.put(new Pair<>(nextNode,reverse),this.getPotentialValue(nextNode));
            this.result.generated++;
        }
        return result;
    }
    /**
     *
     * @param states list of states with weight according to potential function
     * @return sorted array of states to be inserted into open stack "as is"
     */
    protected HashMap<Pair<Node,Operator>, Double> sortByPotential(HashMap<Pair<Node,Operator>,Double> states){
        List<Map.Entry<Pair<Node,Operator>, Double> > list = new LinkedList<Map.Entry<Pair<Node,Operator>, Double> >(states.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Pair<Node,Operator>, Double> >() {
            public int compare(Map.Entry<Pair<Node,Operator>, Double> o1,
                               Map.Entry<Pair<Node,Operator>, Double> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        HashMap<Pair<Node,Operator>, Double> temp = new LinkedHashMap<Pair<Node,Operator>, Double>();
        for (Map.Entry<Pair<Node,Operator>, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    /**
     * inserts all given states into open stack, in the same order as it is in the array.
     * @param states
     */
    protected void insertToOpen(HashMap<Pair<Node,Operator>, Double> states){
        for(Pair<Node,Operator> state:states.keySet()){
            this.openList.push(state);
        }
    }

    /**
     * returns potential value for a given state.
     * @param state
     * @return
     */
    protected double getPotentialValue(Node state) {
        //System.out.println(state.getCurrent().getH()/(this.currentC-state.getG()));
        return state.getCurrent().getH()/(this.currentC-state.getG());
    }

    private SearchResult reconstructPath(Node node){
        SolutionImpl solution = new SolutionImpl();
        solution.setCost(node.getG());
        Node prev = node.getPrevious();
        while (prev.getPrevious()!=null){
            solution.addState(prev.getCurrent());
            prev = prev.getPrevious();
        }
        this.result.addSolution(solution);
        return this.result;
    }

}
