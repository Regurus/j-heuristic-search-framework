package org.cs4j.core.algorithms;

import org.cs4j.core.SearchAlgorithm;
import org.cs4j.core.SearchDomain;
import org.cs4j.core.SearchResult;
import org.cs4j.core.collections.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Daniel on 21/12/2015.
 */
public class DP  implements SearchAlgorithm {

    private static final int open_ID = 1;

    private static final Map<String, Class> DPPossibleParameters;

    // Declare the parameters that can be tuned before running the search
    static
    {
        DPPossibleParameters = new HashMap<>();
        DP.DPPossibleParameters.put("weight", Double.class);
        DP.DPPossibleParameters.put("reopen", Boolean.class);
        DP.DPPossibleParameters.put("FR", Integer.class);
        DP.DPPossibleParameters.put("optimalSolution", Double.class);
    }

    // The domain for the search
    private SearchDomain domain;
    // Open list (frontier)
//    private BinHeapF<Node> open;
    private GH_heap<Node> open;//gh_heap
    //    private BinHeapF<Node> openF;
    // Closed list (seen states)
    private TreeMap<PackedElement, Node> closed;
    //    private Map<PackedElement, Node> closed;
    //the result to return
    private SearchResultImpl result;

    // For Dynamic Potential Bound
    protected double weight;
    // Whether to perform reopening of states
    private boolean reopen;
    //when to empty Focal
    private int FR;
    private NodeComparator NC;
    private NodePackedComparator NPC;

    private double optimalSolution;
    private HashMap<String,Double> coefficients;
    private String name;
    private boolean isFocalized;

    /**
     * Sets the default values for the relevant fields of the algorithm
     */
    private void _initDefaultValues() {
        // Default values
        this.weight = 1.0;
        this.reopen = true;
        this.FR = Integer.MAX_VALUE;
    }


    /**
     * A default constructor of the class
     * @param name the name of the algorithm
     * @param coefficients for the algo
     * @param isFocalized if the algoritm is focalized or not
     */
    public DP(String name, HashMap coefficients, boolean isFocalized) {
        this.coefficients = coefficients;
        this.name = name;
        this.isFocalized = isFocalized;
        this._initDefaultValues();
    }

    @Override
    public String getName() {
/*        if(this.FR==Integer.MAX_VALUE) return "DP";
        else return "DP"+this.FR;*/
        return this.name;
    }

    private void _initDataStructures(SearchDomain domain) {
        this.domain = domain;
        this.result = new SearchResultImpl();
        this.NC = new NodeComparator();
        this.NPC = new NodePackedComparator();
//        this.open = new BinHeapF<>(open_ID,domain,this.NC);
        this.open = new GH_heap<>(weight, open_ID, this.domain.initialState().getH(), NPC, result,coefficients,isFocalized);//no oracle
        //for cases where we want to set the fmin start
        if(this.optimalSolution != 0) {
            this.open.setOptimal(optimalSolution);
        }
//        this.openF = new BinHeapF<>(openF_ID,domain);
        //this.open = buildHeap(heapType, 100);
        this.closed = new TreeMap<>();
//        this.closed = new HashMap<>();
    }

    @Override
    public SearchResult search(SearchDomain domain) {
        Node goal = null;
        Node bestGoalNode = null;
        // Initialize all the data structures required for the search
        this._initDataStructures(domain);

        result.startTimer();
        result.setExtras("numOfGoalsFound",0+"");

        // Let's instantiate the initial state
        SearchDomain.State currentState = domain.initialState();

        // Create a graph node from this state
        Node initNode = new Node(currentState);
        // And add it to the frontier
        _addNode(initNode);

        try{
            while (!this.open.isEmpty() && result.getGenerated() < this.domain.maxGeneratedSize() && result.checkMinTimeOut()) {

                // Take the first state (still don't remove it)
                Node currentNode = _selectNode();


/*                if (currentNode.potential < DP.this.weight - 0.00001) {
//                    System.out.println("\rPotential too low   \tFmin:" + this.open.getFmin() + "\tF:" +currentNode.f + "\tG:" + currentNode.g + "\tH:" + currentNode.h+ "\tPotential:" + currentNode.potential);
                    currentNode.reCalcValue();
                    if (currentNode.potential < DP.this.weight - 0.00001) {
                        System.out.println("Potential after reCalc\tFmin:" + this.open.getFmin() + "\tF:" + currentNode.f + "\tG:" + currentNode.g + "\tH:" + currentNode.h + "\tPotential:" + currentNode.potential);
                    }
                }*/

                // Extract the state from the packed value of the node
                currentState = domain.unpack(currentNode.packed);

                // Check for goal condition
                if (domain.isGoal(currentState)) {
                    double fmin = open.getFmin();
                    if(currentNode.f < fmin*weight){
                        goal = currentNode;
                        break;
                    }
                    else{
                        if(bestGoalNode == null || bestGoalNode.g > currentNode.g){
                            bestGoalNode = currentNode;
                        }
                        TreeMap<String,String> extras = result.getExtras();
                        if(extras.get("generatedFirst") == null){
                            result.setExtras("generatedFirst",result.generated+"");
                        }
                        Double numOfGoalsFound = Double.parseDouble(extras.get("numOfGoalsFound"));
                        result.setExtras("numOfGoalsFound",numOfGoalsFound+1+"");
//                        System.out.print("\n[INFO] A goal was found but not under the bound:"+fmin*weight+" f:"+currentNode.f+", W:"+weight+", fmin:"+fmin);
                    }
                }

                // Expand the current node
                ++result.expanded;
                // Go over all the possible operators and apply them
                for (int i = 0; i < domain.getNumOperators(currentState); ++i) {
                    SearchDomain.Operator op = domain.getOperator(currentState, i);
                    // Try to avoid loops
                    if (op.equals(currentNode.pop)) {
                        continue;
                    }
                    // Here we actually generate a new state
                    ++result.generated;
/*                    if(result.generated >= 4660) {
                        System.out.println("generated: " + result.generated);
                    }*/
                    SearchDomain.State childState = domain.applyOperator(currentState, op);
                    Node childNode = new Node(childState, currentNode, currentState, op, op.reverse(currentState));
/*                    if(childNode.getH() == 4.0 && childNode.getG() == 54 && childNode.getD() == 2){
                        System.out.println("++++++++++++++++++++++");
                        System.out.println(childState.dumpState());
                        System.out.println(result.generated);
                        System.out.println("++++++++++++++++++++++");
                    }
                    if(result.generated > 485){
                        System.out.print("\r");
                    }*/
/*                    if(result.getGenerated() % 1 == 0){
                        DecimalFormat formatter = new DecimalFormat("#,###");
                        System.out.print("\r[INFO] DP Generated:" + formatter.format(result.getGenerated()));
                    }*/

                    // Treat duplicates
                    if (this.closed.containsKey(childNode.packed)) {
                        _duplicateNode(childNode);
                    } else {// Otherwise, the node is new (hasn't been reached yet)
                        _addNode(childNode);
                    }
                }
                _removeNode(currentNode);

                if(bestGoalNode != null && bestGoalNode.f < open.getFmin()*weight){
                    goal = currentNode;
                    break;
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println("[INFO] DP OutOfMemory :-( "+e);
            System.out.println("[INFO] OutOfMemory DP on:"+this.domain.getClass().getSimpleName()+" generated:"+result.getGenerated());
        }

        result.stopTimer();

        // If a goal was found: update the solution
        if (goal != null) {
            System.out.print("\r");
            SearchResultImpl.SolutionImpl solution = new SearchResultImpl.SolutionImpl(this.domain);
            List<SearchDomain.Operator> path = new ArrayList<>();
            List<SearchDomain.State> statesPath = new ArrayList<>();
            // System.out.println("[INFO] Solved - Generating output path.");
            double cost = 0;

            SearchDomain.State currentPacked = domain.unpack(goal.packed);
            SearchDomain.State currentParentPacked = null;
            for (Node currentNode = goal;
                 currentNode != null;
                 currentNode = currentNode.parent, currentPacked = currentParentPacked) {
                // If op of current node is not null that means that p has a parent
                if (currentNode.op != null) {
                    path.add(currentNode.op);
                    currentParentPacked = domain.unpack(currentNode.parent.packed);
                    cost += currentNode.op.getCost(currentPacked, currentParentPacked);
                }
                statesPath.add(domain.unpack(currentNode.packed));
            }
            // The actual size of the found path can be only lower the G value of the found goal
            assert cost <= goal.g;
            double roundedCost = new BigDecimal(cost).setScale(4, RoundingMode.HALF_DOWN).doubleValue();
            double roundedG = new BigDecimal(goal.g).setScale(4, RoundingMode.HALF_DOWN).doubleValue();
            if (roundedCost - roundedG < 0) {
                System.out.println("[INFO] Goal G is higher than the actual cost " +
                        "(G: " + goal.g +  ", Actual: " + cost + ")");
            }

            Collections.reverse(path);
            solution.addOperators(path);

            Collections.reverse(statesPath);
            solution.addStates(statesPath);

            solution.setCost(cost);
            result.addSolution(solution);
        }

        return result;
    }

    @Override
    public Map<String, Class> getPossibleParameters() {
        return DP.DPPossibleParameters;
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
            case "reopen": {
                this.reopen = Boolean.parseBoolean(value);
                break;
            }
            case "FR": {
                this.FR = Integer.parseInt(value);
                break;
            }
            case "optimalSolution": {
                this.optimalSolution = Double.parseDouble(value);
                break;
            }
            default: {
                throw new NotImplementedException();
            }
        }
    }

    /**
     *
     * @return chosen Node for expansion
     */
    private Node _selectNode() {
        Node toReturn;
        toReturn = this.open.peek();
/*        if(openF.getFminCount(FR) < result.generated){
            toReturn = this.openF.peek();
        }*/
        return toReturn;
    }

    /**
     *
     * @param toAdd is the new node toAdd to open
     */
    private void _addNode(Node toAdd) {
        this.open.add(toAdd);
//        this.openF.add(toAdd);
        // The nodes are ordered in the closed list by their packed values
        this.closed.put(toAdd.packed, toAdd);
    }

    /**
     *
     * @param childNode is the new duplicate detected
     */
    private void _duplicateNode(Node childNode) {
        // Count the duplicates
        ++result.duplicates;
        // Get the previous copy of this node (and extract it)
        Node dupChildNode = this.closed.get(childNode.packed);
        // check if the potential has changed
//        dupChildNode.reCalcValue();
        // Take the h value from the previous version of the node (for case of randomization of h values)
        childNode.computeNodeValue(dupChildNode.h,dupChildNode.d);
        // check which child is better
        int compared = NC.compare(childNode, dupChildNode);
        // childNode is better, need to update dupChildNode
        if (compared < 0) {
            // In any case update the duplicate with the new values - we reached it via a shorter path
            _updateNode(dupChildNode,childNode);
        }
    }

    /**
     *
     * @param oldNode is the old Node, last time visited
     * @param newNode is the new Node, with better path
     */
    private void _updateNode(Node oldNode,Node newNode) {
//        double oldF = oldNode.getF();
/*        double oldG = oldNode.getG();
        double oldH = oldNode.getH();
        oldNode.f = newNode.f;
        oldNode.g = newNode.g;
        oldNode.op = newNode.op;
        oldNode.pop = newNode.pop;
        oldNode.parent = newNode.parent;
        oldNode.potential = newNode.potential;*/
        // if dupChildNode is in open, update it there too
        if (oldNode.getIndex(this.open.getKey()) != -1) {
            ++result.opupdated;
            this.open.remove(oldNode);
            _copyNodeValues(oldNode,newNode);
            this.open.add(newNode);
//            this.open.updateF(oldNode,oldG,oldH);//gh_heap
//            this.open.updateF(oldNode, oldF);
//            this.openF.updateF(oldNode, oldF);
        }
        // Otherwise, consider to reopen dupChildNode
        else {
            if (this.reopen) {
                ++result.reopened;
                _copyNodeValues(oldNode,newNode);
                this.open.add(oldNode);//gh_heap
//                this.open.add(oldNode);
//                this.openF.add(oldNode);
            }
        }
        // in any case, update closed to be bestChild
        this.closed.put(oldNode.packed, oldNode);
    }

    /**
     *
     * @param oldNode get values from newNode
     * @param newNode
     */
    private void _copyNodeValues(Node oldNode,Node newNode) {
        oldNode.f = newNode.f;
        oldNode.g = newNode.g;
        oldNode.h = newNode.h;
        oldNode.op = newNode.op;
        oldNode.pop = newNode.pop;
        oldNode.parent = newNode.parent;

        oldNode.d = newNode.d;
        oldNode.sseH = newNode.sseH;
        oldNode.sseD = newNode.sseD;
        oldNode.fHat = newNode.fHat;
        oldNode.hHat = newNode.hHat;
        oldNode.dHat = newNode.dHat;
        oldNode.depth = newNode.depth;
//        oldNode.potential = newNode.potential;
    }

    /**
     *
     * @param toRemove is the node to be removed from open
     */
    private void _removeNode(Node toRemove) {
//        double prevFmin = open.getFmin();
        this.open.remove(toRemove);
//        this.openF.remove(toRemove);
/*        if(prevFmin < open.getFmin()){//fmin changed, need to reorder priority Queue
            _reorder();
        }*/
    }

    /**
     * The node class
     */
    protected final class Node extends SearchQueueElementImpl{
        private double f;
        private double g;
        private double h;
        private double d;
        private double sseH;
        private double sseD;
        private double fHat;
        private double hHat;
        private double dHat;
        private int depth;
//        private double potential;

        private SearchDomain.Operator op;
        private SearchDomain.Operator pop;

        private Node parent;
        private PackedElement packed;
        private int[] secondaryIndex;
        private double fcounterFmin;

        private Node(SearchDomain.State state, Node parent, SearchDomain.State parentState, SearchDomain.Operator op, SearchDomain.Operator pop) {
            // Size of key
            super(2);
            // TODO: Why?
            this.secondaryIndex = new int[1];
            double cost = (op != null) ? op.getCost(state, parentState) : 0;
            // If each operation costs something, we should add the cost to the g value of the parent
            this.g = cost;
            this.depth = 1;
            // Our g equals to the cost + g value of the parent
            if (parent != null) {
                this.g += parent.g;
                this.depth += parent.depth;
            }

            // Update potential, h and f values
            this.computeNodeValue(state.getH(),state.getD());

            // Parent node
            this.parent = parent;
            this.packed = DP.this.domain.pack(state);
            this.pop = pop;
            this.op = op;

            // Compute the actual values of sseH and sseD
            this._computePathHats(parent, cost);

        }

        /**
         * Use the Path Based Error Model by calculating the mean one-step error only along the current search
         * path: The cumulative single-step error experienced by a parent node is passed down to all of its children

         * @return The calculated sseHMean
         */
        private double __calculateSSEMean(double totalSSE) {
            return (this.g == 0) ? totalSSE : totalSSE / this.depth;
        }

        /**
         * @return The mean value of sseH
         */
        private double _calculateSSEHMean() {
            return this.__calculateSSEMean(this.sseH);
        }

        /**
         * @return The mean value of sseD
         */
        private double _calculateSSEDMean() {
            return this.__calculateSSEMean(this.sseD);
        }

        /**
         * @return The calculated hHat value
         *
         * NOTE: if our estimate of sseDMean is ever as large as one, we assume we have infinite cost-to-go.
         */
        private double _computeHHat() {
//            double hHat = Double.MAX_VALUE;
            double hHat = this.h;
            double sseDMean = this._calculateSSEDMean();
            if (sseDMean < 1) {
                double sseHMean = this._calculateSSEHMean();
                hHat = this.h + ( (this.d / (1 - sseDMean)) * sseHMean );
            }
/*            else{
                System.out.println("sseDMean: "+sseDMean);
                hHat = this.h;
            }*/
            return hHat;
        }

        /**
         * @return The calculated dHat value
         *
         * NOTE: if our estimate of sseDMean is ever as large as one, we assume we have infinite distance-to-go
         */
        private double _computeDHat() {
            double dHat = Double.MAX_VALUE;
            double sseDMean = this._calculateSSEDMean();
            if (sseDMean < 1) {
                dHat = this.d / (1 - sseDMean);
            }
            return dHat;
        }

        /**
         * The function computes the values of dHat and hHat of this node, based on that values of the parent node
         * and the cost of the operator that generated this node
         *
         * @param parent The parent node
         * @param edgeCost The cost of the operation which generated this node
         */
        public void _computePathHats(Node parent, double edgeCost) {
            if (parent != null) {
                // Calculate the single step error caused when calculating h and d
                this.sseH = parent.sseH + ((edgeCost + this.h) - parent.h);
                this.sseD = parent.sseD + ((1 + this.d) - parent.d);
/*                if(sseD < 0){
                    System.out.println("sseD: "+sseD);
                    System.out.println("this:"+this);
                    System.out.println("parent:"+parent);
                    SearchDomain.State State = domain.unpack(this.packed);
                    SearchDomain.State parentState = domain.unpack(parent.packed);
                    State.getD();
                    System.out.println("sseD: "+sseD);
                }*/
            }

            this.hHat = this._computeHHat();
            this.dHat = this._computeDHat();
            this.fHat = this.g + this.hHat;

            // This must be true assuming the heuristic is consistent (fHat may only overestimate the cost to the goal)
            if (domain.isCurrentHeuristicConsistent()) {
                assert this.fHat >= this.f;
            }
            assert this.dHat >= 0;
        }

        @Override
        public String toString() {
            SearchDomain.State state = domain.unpack(this.packed);
            StringBuilder sb = new StringBuilder();
            sb.append("State:"+state.dumpStateShort());
            sb.append(", h: "+this.h);//h
            sb.append(", g: "+this.g);//g
            sb.append(", f: "+this.f);//f
            sb.append(", d: "+this.d);//d
//            sb.append(", potential: "+this.potential);//potential
//            sb.append(", fcounterFmin: "+this.fcounterFmin);//fcounterFmin
            return sb.toString();
        }

/*        void printNode(Node node){
            SearchDomain.State state = domain.unpack(node.packed);
            StringBuilder sb = new StringBuilder();
            sb.append("State:"+state.dumpStateShort());
            sb.append(", h: "+this.h);//h
            sb.append(", g: "+this.g);//g
            sb.append(", f: "+this.f);//f
            sb.append(", potential: "+this.potential);//potential
//            sb.append(", fcounterFmin: "+this.fcounterFmin);//fcounterFmin
            System.out.println(sb.toString());
            System.out.println("State:"+state.dumpStateShort());
//            System.out.println("F:" +this.f + "\tG:" + this.g + "\tH:" + this.h+ "\tPotential:" + this.potential+ "\tfcounterFmin:" + this.fcounterFmin);
        }*/

        /**
         * The function computes the F values according to the given heuristic value (which is computed externally)
         *
         * Also, all other values that depend on h are updated
         *
         * @param updatedHValue The updated heuristic value
         */
        public void computeNodeValue(double updatedHValue, double updatedDValue) {

            updatedHValue = new BigDecimal(updatedHValue).setScale(4, RoundingMode.HALF_DOWN).doubleValue();

            if(this.h != 0 && this.h != updatedHValue){
                System.out.println("[INFO] GH_heap should update");
            }
            this.h = updatedHValue;
            this.d = updatedDValue;
            this.f = this.g + this.h;
            this.fcounterFmin = open.getFmin();
//            this.potential =  (this.fcounterFmin*DP.this.weight -this.g)/this.h;
        }

/*        public void reCalcValue() {
            if(open.getFmin() != this.fcounterFmin) {
                this.fcounterFmin = open.getFmin();
                this.potential =  (this.fcounterFmin*DP.this.weight -this.g)/this.h;
            }
        }*/


        /**
         * A constructor of the class that instantiates only the state
         *
         * @param state The state which this node represents
         */
        private Node(SearchDomain.State state) {
            this(state, null, null, null, null);
        }

/*
        @Override
        public void setSecondaryIndex(int key, int index) {
            this.secondaryIndex[key] = index;
        }

        @Override
        public int getSecondaryIndex(int key) {
            return this.secondaryIndex[key];
        }

        @Override
        public double getRank(int level) {
            return (level == 0) ? this.f : this.g;
        }*/

        @Override
        public double getF() {
            return this.f;
        }

        @Override
        public double getG() {
            return this.g;
        }

        @Override
        public double getDepth() {
            return this.depth;
        }

        @Override
        public double getH() {
            return this.h;
        }

        @Override
        public double getD() {
            return this.d;
        }

        @Override
        public double getHhat() {return this.hHat;}

        @Override
        public double getDhat() {return this.dHat;}

        @Override
        public SearchQueueElement getParent() {
            return this.parent;
        }

    }

    /**
     * The nodes comparator class
     */
    protected final class NodeComparator implements Comparator<Node> {

        @Override
        public int compare(final Node a, final Node b) {
            // First compare by f (smaller is preferred), then by h (smaller is preferred)

            if (a.f < b.f) return -1;
            if (a.f > b.f) return 1;

            if (a.h < b.h) return -1;
            if (a.h > b.h) return 1;

            if (a.d < b.d) return -1;
            if (a.d > b.d) return 1;

            return 0;
        }
    }

    /**
     * The nodes comparator class
     */
    protected final class NodePackedComparator implements Comparator<Node> {

        @Override
        public int compare(final Node a, final Node b) {
            if (a.packed.equals(b.packed)) return 0;
            return -1;
        }
    }
}
