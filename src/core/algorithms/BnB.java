package core.algorithms;

import java.util.*;

import core.SearchAlgorithm;
import core.*;
import core.algorithms.SearchResultImpl.SolutionImpl;
import core.SearchResult;
import core.collections.PackedElement;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Recursive Best-First Search
 *
 */
public class BnB extends SearchAlgorithm {

    class BnBNodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node a, Node b) {
            return Double.compare( b.getG() + b.getCurrent().getH(), a.getG() + a.getCurrent().getH());
        }

    }


    private SearchResultImpl result;
    private SearchDomain domain;
//    private Node goal;
    protected double weight; //weight for heuristic
    private double boundFactor; //above this bound, cut the branch
    public double userLimitboundFactor;
    private Stack<Node> open; //open list, nodes to check
    private HashMap<PackedElement, Double> visited; // nodes already visited
    private double fMin; //under this number we can return the goal directly


    private List<Operator> path = new ArrayList<Operator>();

    public BnB() {
        this.weight = 1;
        this.userLimitboundFactor = Double.POSITIVE_INFINITY;
        this.fMin = 0;
    }

    public BnB(double weight, double limit) {
        this.weight = weight;
        this.userLimitboundFactor = limit;
        this.fMin = 0;
    }

    public BnB(double weight, double limit,int maxExpanded) {
        this.weight = weight;
        this.userLimitboundFactor = limit;
        this.fMin = 0;
        this.maxExpanded = maxExpanded;
    }

    @Override
    public String getName() {
        return "BnB";
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
        //initialize params
        open = new Stack<>();
        Node goal = null;
        visited = new HashMap<>();
        this.domain = domain;
        result = new SearchResultImpl();
        result.startTimer();
        this.boundFactor = Double.POSITIVE_INFINITY;
        if (this.boundFactor >= this.userLimitboundFactor) {
            this.boundFactor = this.userLimitboundFactor;
        }

        State initialState = domain.initialState();
        Node initialNode = new Node(null, initialState, 0, null);
        open.add(initialNode);
        visited.put(domain.pack(initialState), 0.0);
        double nextIterationFMin = Double.POSITIVE_INFINITY; //fmin of current iteration

        while (!open.isEmpty()) {
            Node currentNode = open.pop();
            State state = currentNode.getCurrent();
            if (domain.isGoal(state)) {

                if(boundFactor >= currentNode.getG()){
                    goal = currentNode;
                    boundFactor = goal.getG();
//                    if(goal.getG() <= fMin){
//                        break;
//                    }
                }
                continue;
            }

            visited.remove(domain.pack(state));

            if(getF(currentNode)> boundFactor){
                //unnecessary because we found goal and wouldn't be another iteration , i put it in remarks
//                if(getF(currentNode) < nextIterationFMin){
//                    nextIterationFMin = getF(currentNode);
//                }
                continue;
            }

            //expand node
            result.expanded++;
            if(result.expanded>maxExpanded)
                return result;
            int numOperators = domain.getNumOperators(state);
            List<Node> priority = new ArrayList<>();
            for(int i=0; i< numOperators; ++i) {
                Operator op = domain.getOperator(state, i);
                State childState = domain.applyOperator(state, op);

                if(currentNode.getPrevious()!= null && childState.equals(currentNode.getPrevious().getCurrent())){
                    continue;
                }

                PackedElement childPack = domain.pack(childState);
                double child_g = currentNode.getG()+op.getCost(childState, state);

                if (visited.containsKey(childPack)) {
                    if (visited.get(childPack) < child_g) {
                        continue;
                    }
                    else {
                        visited.put(childPack, child_g);
                    }
                }
                result.generated++;

                Node childNode = new Node(currentNode, childState, child_g, op);
                //add to open stack. else: cut the branch
                if (getF(childNode) > boundFactor) {
                    if(getF(childNode) < nextIterationFMin){
                        nextIterationFMin = getF(childNode);
                    }
                    continue;
                }
                priority.add(childNode);

            }
            priority.sort(new BnBNodeComparator());
            open.addAll(priority);
        }
        //TODO: finish running the algorithm. restore path
//        if (goal != null) {
//            SolutionImpl solution = new SolutionImpl();
//            for (Node p = goal; p != null; p = p.getPrevious()) {
//                //path.add(p);
//            }
//            Collections.reverse(path);
//            solution.addOperators(path);
//            solution.setCost(goal.getG());
//            result.addSolution(solution);
//        }

        if (goal != null) {
            SolutionImpl solution = new SolutionImpl();
            for (Node p = goal; p != null; p = p.getPrevious()) {
                path.add(p.op);
            }
            Collections.reverse(path);
            solution.addOperators(path);
            solution.setCost(goal.getG());
            result.addSolution(solution);
        }
        fMin = nextIterationFMin;
        result.stopTimer();
        return result;
    }

    private double getF(Node n){
        return n.getG()+n.getCurrent().getH()*weight;
    }
}