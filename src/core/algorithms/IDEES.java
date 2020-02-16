package core.algorithms;

import core.Operator;
import core.SearchDomain;
import core.SearchResult;
import core.State;

import java.util.*;

/**
 * Iterative Deepening Explicit Estimated Search
 *
 * @Implementor Lior Yakobson
 */
public class IDEES {
    private final int MAX_BUCKETS = 50;

    // The domain for the search
    private SearchDomain domain;

    private SearchResultImpl result;
    private SearchResultImpl.SolutionImpl solution;

    private double weight;
    private double k;
    private double b;
    private double nodesExpanded;
    private double totalChildren;

    private List<Node> openBestF;
    private List<Node> openBestFHat;
    private List<Node> openBestDHat;

    private Node incumbent;
    private double incF;
    private double tFHat;
    private double tLHat;
    private double minF;
    private double minFNext;

    private double[]  dataFHat;
    private double[]  dataLHat;

    public IDEES(){
        this(1.0);
    }

    public IDEES(double weight){
        this.weight = weight;
    }

    public String getName() {
        return "IDEES";
    }

    private void _initDataStructures(){
        openBestF = new ArrayList<>();
        openBestFHat = new ArrayList<>();
        openBestDHat = new ArrayList<>();
    }

    private Node _selectNode(){
        Node toReturn;
        Node bestF = openBestF.get(openBestF.size()-1);
        Node bestFHat = openBestFHat.get(openBestFHat.size()-1);
        Node bestDHat = openBestDHat.get(openBestDHat.size()-1);
        if(bestDHat.fHat <= weight*bestF.f){
            toReturn = bestDHat;
        }
        else if(bestFHat.fHat <= weight * bestF.f){
            toReturn = bestFHat;
        }
        else{
            toReturn = bestF;
        }
        //Here we remove the returned node from all three queues. (But what it doesnt exist in all 3 queues??)
        openBestF.removeAll(Collections.singleton(toReturn));
        openBestFHat.removeAll(Collections.singleton(toReturn));
        openBestDHat.removeAll(Collections.singleton(toReturn));

        return toReturn;
    }

    private void _expandNode(Node node){
        ArrayList<Node> tempList = new ArrayList<>();
        int numOps = domain.getNumOperators(node.state);
        //Initiate all the nodes of the father
        for(int i=0;i<numOps;i++){
            Operator op = domain.getOperator(node.state,i);
            Operator pop = op.reverse(node.state);
            State newState = domain.applyOperator(node.state, op);

            tempList.add(new Node(newState, node, node.state, op, pop));
        }

        //Sort them by their F and add them to the relevant DataStructure
        tempList.sort((Node n1, Node n2) ->{
            if(n1.f < n2.f)
                return 1;
            else
                return -1;
        });
        openBestF.addAll(tempList);

        //Sort them by their fHat and add them to the relevant DataStructure
        tempList.sort((Node n1, Node n2) ->{
            if(n1.fHat < n2.fHat)
                return 1;
            else
                return -1;
        });
        openBestFHat.addAll(tempList);

        //Sort them by their dHat and add them to the relevant DataStructure
        tempList.sort((Node n1, Node n2) ->{
            if(n1.dHat < n2.dHat)
                return 1;
            else
                return -1;
        });
        openBestDHat.addAll(tempList);
    }

    public Node search(SearchDomain domain){
        // Init all the queues relevant to search (destroy previous results)
        this._initDataStructures();

        this.domain = domain;
        k = 0; //Iteration number

        try{
            // Create the initial state and node
            State initState = domain.initialState();
            Node initNode = new Node(initState, null, null, null, null);
            // Insert the initial node into all the lists
            openBestF.add(initNode);
            openBestDHat.add(initNode);
            openBestFHat.add(initNode);

            incumbent = null;
            incF = Double.MAX_VALUE;
            tFHat = initNode.h;
            tLHat = initNode.d;
            minF = Double.MAX_VALUE;

            nodesExpanded = 0;
            totalChildren = 0;

            while(incF > weight*minF){
                k++;
                _resetBuckets();

                minFNext = Double.MAX_VALUE;
                if(DFS(initNode))
                    break;
                minF = minFNext;

                b = totalChildren/nodesExpanded;
                tFHat = updateData(dataFHat, tFHat);
                tLHat = updateData(dataLHat, tLHat);
            }
        }
        catch (Exception e) {
//            System.out.println("[INFO] EES OutOfMemory :-( "+e);
//            System.out.println("[INFO] OutOfMemory EES on:"+this.domain.getClass().getSimpleName()+" generated:"+result.getGenerated());
            e.printStackTrace();
        }

        return incumbent;
    }

    private void _resetBuckets() {
        dataFHat = new double[MAX_BUCKETS];
        dataLHat = new double[MAX_BUCKETS];

        for(int i=0; i<50; i++){
            dataFHat[i] = 0;
            dataLHat[i] = 0;
        }
    }

    private boolean DFS(Node n){
        if(domain.isGoal(n.state)){ //Lines 11-14
            if(n.f < incF){
                incumbent = n;
                incF = n.f;
            }
            return incF <= weight*minF;
        }
        else if((incF == Double.MAX_VALUE) && ((n.fHat > weight*tFHat) || (n.depth + n.dHat > tLHat))){ //Lines 15-17
            pruneNode(n);
            minFNext = Math.min(minFNext, n.f);
        }
        else if(incF < Double.MAX_VALUE && incF <= weight*n.f){ //Lines 18-19
            pruneNode(n);
        }
        else{ //Lines 20-24
            _expandNode(n);
            int children = domain.getNumOperators(n.state);
            nodesExpanded++;
            totalChildren += children;

            for(int i=0;i<children;i++){
                Node child = _selectNode();

                if(DFS(child))
                    return true;
            }
        }
        return false;
    }

    private void pruneNode(Node n){
        for(int i=0;i<50;i++){
            double btmLimit = 1 + (double)i/100;
            double upLimit = 1 + (double)(i+1)/100;

            if((tFHat*btmLimit < n.fHat) && (n.fHat <= tFHat*upLimit))
                dataFHat[i]++;
            double nLHat = n.depth + n.dHat;
            if((tLHat*btmLimit < nLHat) && (nLHat <= tLHat*upLimit))
                dataLHat[i]++;
        }
    }

    private double updateData(double[] bucketsArr, double defaultVal) {
        int count = 0;
        int bound = (int) Math.pow(b, k);
        double res = 0;

        for (int i=0; i<50; i++) {
            count += bucketsArr[i];
            if (count >= bound) {
                res = 1 + (double)i/100;
                break;
            }
        }

        if (res == 0) {
            res = 1.5;
        }
        return defaultVal*res;
    }

    private class Node{
        public double f;
        public double g;
        public double d;
        public double h;
        public double depth;

        public Operator op;
        public Operator pop;
        public Node parent;
        public State state;

        private double sseH;
        private double sseD;
        public double fHat;
        public double hHat;
        public double dHat;

        public Node(State state, Node parent, State parentState, Operator op, final Operator pop){
            this.state = state;
            this.op = op;
            this.pop = pop;
            this.parent = parent;

            // Calculate the cost of the node:
            double cost = (op != null) ? op.getCost(state, parentState) : 0;
            this.g = cost;
            this.depth = 1;
            // Our g equals to the cost + g value of the parent
            if (parent != null) {
                this.g += parent.g;
                this.depth += parent.depth;
            }

            this.h = state.getH();

            // Start of PathMax
            if (parent != null) {
                double costsDiff = this.g - parent.g;
                this.h = Math.max(this.h, (parent.h - costsDiff));
            }
            // End of PathMax

            this.d = state.getD();
            this.f = this.g + this.h;

            // Default values
            this.sseH = 0;
            this.sseD = 0;

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
            double hHat = Double.MAX_VALUE;
            double sseDMean = this._calculateSSEDMean();
            if (sseDMean < 1) {
                double sseHMean = this._calculateSSEHMean();
                hHat = this.h + ( (this.d / (1 - sseDMean)) * sseHMean );
            }
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
        private void _computePathHats(Node parent, double edgeCost) {
            if (parent != null) {
                // Calculate the single step error caused when calculating h and d
                this.sseH = parent.sseH + ((edgeCost + this.h) - parent.h);
                this.sseD = parent.sseD + ((1 + this.d) - parent.d);
                if(sseD < 0){
//                    System.out.println("sseD: "+sseD);
//                    System.out.println("this:"+this);
//                    System.out.println("parent:"+parent);
                    sseD = 0;
                }
            }

            this.hHat = this._computeHHat();
            this.dHat = this._computeDHat();
            this.fHat = this.g + this.hHat;

            // This must be true assuming the heuristic is consistent (fHat may only overestimate the cost to the goal)
            assert !domain.isCurrentHeuristicConsistent() || this.fHat >= this.f;
            assert this.dHat >= 0;
        }

        public int compareTo(Node other) {
            // Nodes are compared by default by their f value (and if f values are equal - by g value)

            // F value: lower f is better
            int diff = (int) (this.f - other.f);
            if (diff == 0) {
                // G value: higher g is better
                return (int) (other.g - this.g);
            }
            return diff;
        }
    }



}
