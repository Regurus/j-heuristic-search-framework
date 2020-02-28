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
            return Double.compare( b.getCurrent().getD(), a.getCurrent().getD());
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


    private List<Operator> path = new ArrayList<Operator>();

    public BnB() {
        this.weight = 1;
        this.userLimitboundFactor = Double.MAX_VALUE;
    }

    public BnB(double weight) {
        this.weight = weight;
        this.userLimitboundFactor = Double.MAX_VALUE;
    }

    public BnB(double weight, double limit) {
        this.weight = weight;
        this.userLimitboundFactor = limit;
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
        this.boundFactor = Double.MAX_VALUE;
        if (this.boundFactor >= this.userLimitboundFactor) {
            this.boundFactor = this.userLimitboundFactor;
        }

        State initialState = domain.initialState();
        Node initialNode = new Node(null, initialState, 0, null);
        open.add(initialNode);
        visited.put(domain.pack(initialState), 0.0);

        while (!open.isEmpty()) {
            Node currentNode = open.pop();
            State state = currentNode.getCurrent();
            if (domain.isGoal(state)) {
//                System.out.println("Found!!!!: G:" + currentNode.getG() +" generated:" + result.generated );
                if(boundFactor >= currentNode.getG()){
                    goal = currentNode;
                    boundFactor = currentNode.getG();
                    break;
                }
                continue;
            }

            visited.remove(domain.pack(state));
            if(currentNode.getG()+state.getH()*weight> boundFactor){
                continue;
            }

            //expand node
            result.expanded++;
            int numOperators = domain.getNumOperators(state);
            List<Node> priority = new ArrayList<>();
            for(int i=0; i< numOperators; ++i) {
                Operator op = domain.getOperator(state, i);
                State childState = domain.applyOperator(state, op);

                if(currentNode.getPrevious()!= null && childState.equals(currentNode.getPrevious().getCurrent())){
//                    System.out.println("WAS HERE!!!");
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

                //add to open stack. else: cut the branch
                if (childState.getH()*weight + child_g > boundFactor) {
                    continue;
                }

                Node childNode = new Node(currentNode, childState, child_g, op);
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
        result.stopTimer();
        return result;
    }
}