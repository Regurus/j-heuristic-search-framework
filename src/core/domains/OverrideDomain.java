package core.domains;

import core.Operator;
import core.SearchDomain;
import core.State;
import core.collections.PackedElement;
import org.apache.log4j.Logger;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import java.util.Map;

/**
 * class for building a graph containing arbitrary search graph not related to any actual domain
 * terms; index: row number in adj. matrix
 */
public class OverrideDomain implements SearchDomain {

    private DefaultUndirectedGraph tree = new DefaultUndirectedGraph(DefaultEdge.class);
    private Node[] nodes;
    private int startIndex = 0;
    public static Logger log = Logger.getLogger(RubiksCube.class.getName());

    /**
     * Basic constructor class using in-memory data
     * !!!same order in transition matrix as in vertex list!!!
     * @param transitionMatrix gValue[i][j] cost for transitioning from i=>j, -1 for no edge there
     * @param vertices list of vertex hValues, -1 for starting node, -2 for goal node
     *
     */
    public OverrideDomain(int[][] transitionMatrix,int[] vertices) {
        //basic input checks
        if(!this.sanityChecks(transitionMatrix, vertices))
            System.out.println("Input Error @constructor");
        nodes = new Node[vertices.length];
        this.addVertices(vertices);
        this.addEdges(transitionMatrix);
    }

    private void addVertices(int[] vertices) {
        for(int i=0;i<vertices.length;i++){
            Node next = new Node(vertices[i]==-2,vertices[i]==-1,vertices[i],i);
            tree.addVertex(next);
            if(next.isRoot()){
                this.startIndex = i;
            }
            nodes[i]=next;
        }
    }

    private void addEdges(int[][] adjacencyMatrix){
        for(int i=0;i<adjacencyMatrix.length;i++){
            for(int j=0;j<adjacencyMatrix[0].length;j++){
                if(adjacencyMatrix[i][j] > 0 && i != j)//no self edges
                    tree.addEdge(nodes[i],nodes[j], new Cost(adjacencyMatrix[i][j]));
            }
        }
    }
    public void printDomain(){
        System.out.println(this.tree.toString());
    }
    private boolean sanityChecks(int[][] transitionMatrix, int[] vertices){
        //sizes fit
        if(transitionMatrix==null || transitionMatrix.length!=transitionMatrix[0].length || transitionMatrix.length!=vertices.length)
        {
            throw new IllegalArgumentException("Transition matrix/vertex array size error!");
        }
        //no more than one start node
        int starts = 0;
        int goals = 0;
        for(int cell:vertices) {
                if(cell<-2)
                    throw new IllegalArgumentException("Max negative value is -2 for goal node!");
                if(cell==-1)
                    starts++;
                if(cell==-2)
                    goals++;
            if(starts>1)
                throw new IllegalArgumentException("Only one starting state allowed!");
        }
        for(int[] row:transitionMatrix) {
            for(int cell:row){
                if(cell<0)
                    throw new IllegalArgumentException("Negative weights now allowed!");
            }
        }
        if(goals==0||starts==0)
            throw new IllegalArgumentException("There should be at least one goal node and one starting node");
        return true;
    }
    @Override
    public State initialState() {
        return new OverrideState(this.nodes[this.startIndex],null,0);
    }

    @Override
    public boolean isGoal(State state) {
        if(!(state instanceof OverrideState)){
            return false;
        }
        return ((OverrideState) state).contained.isGoal;
    }

    @Override
    public int getNumOperators(State state) {
        if(!(state instanceof OverrideState)){
            return 0;
        }
        return tree.degreeOf(((OverrideState)state).contained);
    }

    @Override
    public Operator getOperator(State state, int index) {
        if(!(state instanceof OverrideState)){
            return null;
        }
        Node end;
        Object edge = this.tree.edgesOf(((OverrideState) state).contained).toArray()[index];
        //due to the fact that graph is undirected no way of knowing which one is target and which is source
        Node node1 = (Node)this.tree.getEdgeSource(edge);
        Node node2 = (Node)this.tree.getEdgeTarget(edge);
        if(node1==((OverrideState) state).contained){
            end = node2;
        }
        else {
            end = node1;
        }
        return new TransitionOperator((OverrideState)state,end,((Cost)edge).cost);
    }

    @Override
    public State applyOperator(State state, Operator op) {
        if(!(state instanceof OverrideState) || !(op instanceof TransitionOperator)){
            return null;
        }
        if(debugMode)
            log.debug("index: "+((TransitionOperator) op).start.contained.getIndex()+" to: "+((TransitionOperator) op).end.contained.getIndex());
        return new OverrideState(((TransitionOperator) op).end.contained,
                (OverrideState)state,((TransitionOperator) op).edgeCost+((OverrideState) state).costSoFar);
    }

    @Override
    public State copy(State state) {
        if(!(state instanceof OverrideState)){
            return null;
        }
        return new OverrideState(((OverrideState) state).contained,((OverrideState) state).previous,
                ((OverrideState) state).costSoFar);
    }
    //TODO double check
    @Override
    public PackedElement pack(State state) {
        if(!(state instanceof OverrideState)){
            return null;
        }
        return (OverrideState)state;
    }

    @Override
    public State unpack(PackedElement packed) {
        if(!(packed instanceof OverrideState)){
            return null;
        }
        return (OverrideState)packed;
    }

    @Override
    public String dumpStatesCollection(State[] states) {
        return null;
    }

    @Override
    public boolean isCurrentHeuristicConsistent() {
        return false;
    }

    @Override
    public void setOptimalSolutionCost(double cost) {
    }

    @Override
    public double getOptimalSolutionCost() {
        return -1;
    }

    @Override
    public int maxGeneratedSize() {
        return 10000;
    }

    @Override
    public Map<String, Class> getPossibleParameters() {
        return null;
    }

    @Override
    public void setAdditionalParameter(String parameterName, String value) {

    }
    private class Node{
        boolean isGoal;
        boolean isRoot;
        int hValue;


        int index;

        public Node(boolean isGoal, boolean isRoot, int hValue, int index) {
            this.isGoal = isGoal;
            this.isRoot = isRoot;
            this.hValue = hValue;
            this.index = index+1;
        }

        public boolean isGoal() {
            return isGoal;
        }

        public boolean isRoot() {
            return isRoot;
        }

        public int gethValue() {
            if(this.hValue>=0)
                return hValue;
            return 0;
        }

        public int getIndex() {
            return index;
        }
        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Node){
                return this.index==((Node) obj).index;
            }
            return false;
        }
    }

    private class OverrideState extends PackedElement implements State{
        public final Node contained;
        public final OverrideState previous;
        public final int costSoFar;

        public OverrideState(Node contained, OverrideState previous,int costSoFar) {
            super(1);
            this.contained = contained;
            this.previous = previous;
            this.costSoFar = costSoFar;
        }
        @Override
        public State getParent() {
            return previous;
        }

        @Override
        public double getH() {
            return contained.gethValue();
        }

        @Override
        public double getD() {
            return costSoFar;
        }

        @Override
        public String convertToString() {
            return contained.toString();
        }

        @Override
        public String convertToStringShort() {
            return "index: "+contained.index+" H:"+contained.hValue;
        }

        @Override
        public boolean equals(Object object) {
            if(object instanceof OverrideState){
                return this.contained.equals(((OverrideState) object).contained);
            }
            return false;
        }
    }
    private class TransitionOperator implements Operator{
        private final OverrideState start;
        private final OverrideState end;
        private final int edgeCost;

        public TransitionOperator(OverrideState start, Node end,int edgeCost) {
            this.start = start;
            this.end = new OverrideState(end,this.start,this.start.costSoFar+edgeCost);
            this.edgeCost = edgeCost;
        }

        @Override
        public double getCost(State state, State parent) {
            if(!(state instanceof OverrideState) || !(parent instanceof OverrideState)){
                return Double.MAX_VALUE;
            }
            return ((OverrideState) state).costSoFar - ((OverrideState) state).previous.costSoFar;
        }

        @Override
        public Operator reverse(State state) {
            if(!(state instanceof OverrideState)){
                return null;
            }
            return new TransitionOperator(this.end, this.start.contained,this.edgeCost);
        }

        @Override
        public boolean equals(Object other) {
            if(other instanceof  TransitionOperator){
                return this.start.equals(((TransitionOperator) other).start)
                        && this.end.equals(((TransitionOperator) other).end);
            }
            return false;
        }
    }
    //jgrapht disregards same objects bound to edges so there is a need for container for edge-cost value
    private class Cost{
        int cost;

        public Cost(int cost) {
            this.cost = cost;
        }

        @Override
        public String toString() {
            return "Edge cost:"+cost;
        }
    }
}
